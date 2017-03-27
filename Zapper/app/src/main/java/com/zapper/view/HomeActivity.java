/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This code has some modifications to the original 
// See http://developer.android.com/guide/components/fragments.html
// for a detailed discussion on the app
// I don't recommend toast as debug for flow but why not do that to get started.
// Better to use Log.d() which we introduced before. Toast is fleeting and logs 
// will always in in the LogCat -- hence they are more useful and better practice;
// but you can't see them on the phone. It is sort cool to see onCreate() toast
// as you flip the phone's orientation. It reinforces the lifecycle and the 
// automatic adjustment of the UI.
//
// ATC 2013

package com.zapper.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zapper.R;
import com.zapper.model.backendService.CallerContactDetails;
import com.zapper.model.backendService.ContactDetailsListener;
import com.zapper.model.backendService.Contacts;
import com.zapper.model.backendService.ContactsListener;
import com.zapper.model.beans.Contact;
import com.zapper.model.beans.ContactDetails;
import com.zapper.model.handlers.Connectivity;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

// Demonstration of using fragments to implement different activity layouts.
// This a different layout (and activity flow) when run in
// landscape.

public class HomeActivity extends AppCompatActivity {

	private static final String TAG = HomeActivity.class.getSimpleName();

	private static String baseURL = "http://demo4012764.mockable.io/";

	private static ProgressDialog pDialog;
	private static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// initialize Application Default
		init();

		// Sets the view. Depending on orientation it will select either
		// res/layout/fragment_layout.xml (portrait mode) or
		// res/layout-land/fragment_layout.xml (landscape mode). This is done
		// automatically by the system.
		setContentView(R.layout.activity_fragment_layout);

	}

	private void init() {
		activity = this;
		// initialize loading Dialog
		pDialog = new ProgressDialog(this);
		pDialog.setMessage(getString(R.string.loading));
		pDialog.setCancelable(false);
	}

	// This is the "top-level" fragment, showing a list of items that the user
	// can pick. Upon picking an item, it takes care of displaying the data to
	// the user as appropriate based on the current UI layout.

	// Displays a list of items that are managed by an adapter similar to
	// ListActivity. It provides several methods for managing a list view, such
	// as the onListItemClick() callback to handle click events.

	public static class TitlesFragment extends ListFragment {
		boolean mDualPane;
		int mCurCheckPosition = 0;
		private List<Contact> localContacts;
		private int contactID;
		private String CONTACT_LIST_KEY = "contactList";
		private static Timer mtimer;
		private boolean isFirstTime = true;

		// onActivityCreated() is called when the activity's onCreate() method
		// has returned.

		@Override
		public void onActivityCreated(final Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			// You can use getActivity(), which returns the activity associated
			// with a fragment.
			// The activity is a context (since Activity extends Context) .

			String contactListJSONStr = getFromLocalStorage(CONTACT_LIST_KEY);

			if(contactListJSONStr != null) {
				backupContact(contactListJSONStr);
			}

			// calling Periodical every 20 Sec
			callPeriodical(savedInstanceState, 20 * 1000);

			// initialize refresh fab button
			FloatingActionButton fab = (FloatingActionButton) activity
					.findViewById(R.id.fab);

			// Action for Refresh fab button
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//Call API to get all contacts
					Caller(savedInstanceState);
				}
			});
		}

		private void callPeriodical(final Bundle savedInstanceState, final long time)
		{
			if(mtimer != null) {
				// Rest a repetition timer
				mtimer.cancel();
			}else
			{
				//Call API to get all contacts
				Caller(savedInstanceState);
			}

			// initialize timer
			mtimer = new Timer();
			mtimer.schedule(new TimerTask() {
				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(Connectivity.isConnect(activity)) {
								//Call API to get all contacts
								Caller(savedInstanceState);
								isFirstTime = true;
							}else
							{
								if(isFirstTime) {
									//open internet alert dialog
									openDialog();
									isFirstTime = false;
								}
							}
							callPeriodical(savedInstanceState, time);
						}
					});
				}
			}, time);
		}

		private void backupContact(String contactListJSONStr) {
			// Parse contact list
			if(contactListJSONStr != null) {
				Type listType = new TypeToken<List<Contact>>() {
				}.getType();
				List<Contact> contactList = new Gson().fromJson(contactListJSONStr, listType);

				String[] titles = new String[0];
				if (contactList != null) {
					titles = new String[contactList.size()];
					for (int i = 0; i < contactList.size(); i++) {
						titles[i] = contactList.get(i).getFirstName() + " "
								+ contactList.get(i).getLastName();
					}
				}

				// set contact list in list Adapter
				setListAdapter(new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_activated_1,
						titles));
			}
		}

		private void initFragment(Bundle savedInstanceState) {
			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			// R.id.details relates to the res/layout-land/fragment_layout.xml
			// This is first created when the phone is switched to landscape
			// mode

			View detailsFrame = null;
			try {
				detailsFrame = getActivity().findViewById(R.id.details);
			}catch (Exception e)
			{}
			// Check that a view exists and is visible
			// A view is visible (0) on the screen; the default value.
			// It can also be invisible and hidden, as if the view had not been
			// added.

			mDualPane = detailsFrame != null
					&& detailsFrame.getVisibility() == View.VISIBLE;

			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			}

			if (mDualPane) {
				// In dual-pane mode, the list view highlights the selected
				// item.
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				// Make sure our UI is in the correct state.
				showDetails(mCurCheckPosition);
			} else {
				// We also highlight in uni-pane just for fun
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				getListView().setItemChecked(mCurCheckPosition, true);
			}
		}

		private void Caller(final Bundle savedInstanceState) {
			try {
				//open loading dialog
				pDialog.show();
			}catch (Exception e)
			{}

			if(!Connectivity.isConnect(activity))//if not Connected
			{
				//close loading dialog
				pDialog.hide();
				//open internet alert dialog
				openDialog();
				isFirstTime = false;
			}

			//initialize API to get all contacts
			Contacts contactsCaller = new Contacts(baseURL, TAG);
			//Call Back Listener for API to get all contacts
			contactsCaller.setCallBackListener(new ContactsListener() {
				@Override
				public void onContactAvailable(List<Contact> contacts) {
					localContacts = contacts;

					if (contacts != null)
					{
						// store contact list in local storage if it's  not null
						String contactListJSONStr = new Gson().toJson(contacts);
						setInLocalStorage(CONTACT_LIST_KEY, contactListJSONStr);
					}

					String[] titles;
					if(contacts != null) {
						titles = new String[contacts.size()];
						for (int i = 0; i < contacts.size(); i++) {
							titles[i] = contacts.get(i).getFirstName() + " "
									+ contacts.get(i).getLastName();
						}
						try {
							setListAdapter(new ArrayAdapter<String>(getActivity(),
									android.R.layout.simple_list_item_activated_1,
									titles));
						}catch (Exception ignored)
						{}
					}
					try {
						initFragment(savedInstanceState);
					}catch (Exception ignored)
					{}

					pDialog.hide();
				}
			});
		}

		private void setInLocalStorage(String key, String jsonStr) {
			// set data in local storage
			PreferenceManager.getDefaultSharedPreferences(activity)
					.edit().putString(key, (String) jsonStr).commit();
		}

		private String getFromLocalStorage(String key) {
			// get data from local storage
			return PreferenceManager.getDefaultSharedPreferences(activity)
					.getString(key, null);
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt("curChoice", mCurCheckPosition);
		}

		// If the user clicks on an item in the list for any one the
		// onListItemClick() method is called. It calls a helper function in
		// this case.

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			showDetails(position);
		}

		// Helper function to show the details of a selected item, either by
		// displaying a fragment in-place in the current UI, or starting a whole
		// new activity in which it is displayed.

		void showDetails(int index) {
			if(localContacts != null) {
				// Get ID for a contact to able to call it to get derails
				contactID = localContacts.get(index).getId();
			}

			mCurCheckPosition = index;

			// The basic design is mutli-pane (landscape on the phone) allows us
			// to display both fragments (titles and details) with in the same
			// activity; that is HomeActivity -- one activity with two
			// fragments.
			// Else, it's single-pane (portrait on the phone) and we fire
			// another activity to render the details fragment - two activities
			// each with its own fragment .
			//
			if (mDualPane) {
				// We can display everything in-place with fragments, so update
				// the list to highlight the selected item and show the data.
				// We keep highlighted the current selection
				getListView().setItemChecked(index, true);

				// Check what fragment is currently shown, replace if needed.
				DetailsFragment details = (DetailsFragment) getFragmentManager()
						.findFragmentById(R.id.details);
				if (details == null || details.getShownIndex() != contactID) {
					// Make new fragment to show this selection.

					details = DetailsFragment.newInstance(contactID);

					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.replace(R.id.details, details);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
				}

			} else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				// That is: if this is a single-pane (e.g., portrait mode on a
				// phone) then fire DetailsActivity to display the details
				// fragment

				// Create an intent for starting the DetailsActivity
				Intent intent = new Intent();

				// explicitly set the activity context and class
				// associated with the intent (context, class)
				intent.setClass(getActivity(), DetailsActivity.class);

				// pass the current position
				intent.putExtra("index", contactID);

				startActivity(intent);
			}
		}
	}

	// This is the secondary fragment, displaying the details of a particular
	// item.

	public static class DetailsFragment extends Fragment {

		// Create a new instance of DetailsFragment, initialized to show the
		// text at 'index'.

		public static DetailsFragment newInstance(int index) {
			DetailsFragment f = new DetailsFragment();

			// Supply index input as an argument.
			Bundle args = new Bundle();
			args.putInt("index", index);
			f.setArguments(args);

			return f;	
		}

		public int getShownIndex() {
			return getArguments().getInt("index", 0);
		}

		// The system calls this when it's time for the fragment to draw its
		// user interface for the first time. To draw a UI for your fragment,
		// you must return a View from this method that is the root of your
		// fragment's layout. You can return null if the fragment does not
		// provide a UI.

		// We create the UI with a scrollview and text and return a reference to
		// the scroller which is then drawn to the screen

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			//
			// if (container == null) {
			// // We have different layouts, and in one of them this
			// // fragment's containing frame doesn't exist. The fragment
			// // may still be created from its saved state, but there is
			// // no reason to try to create its view hierarchy because it
			// // won't be displayed. Note this is not needed -- we could
			// // just run the code below, where we would create and return
			// // the view hierarchy; it would just never be used.
			// return null;
			// }

			// If non-null, this is the parent view that the fragment's UI
			// should be attached to. The fragment should not add the view
			// itself, but this can be used to generate the LayoutParams of
			// the view.
			//

			// programmatically create a scrollview and texview for the ageText in
			// the container/fragment layout. Set up the properties and add the
			// view.

			ScrollView scroller = new ScrollView(getActivity());
			TextView text = new TextView(getActivity());
			int padding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, getActivity()
							.getResources().getDisplayMetrics());
			text.setPadding(padding, padding, padding, padding);
			text.setTextSize(16);
			scroller.addView(text);


//			text.setText(Shakespeare.DIALOGUE[getShownIndex()]);

			Spanned spanned = Html.fromHtml("<font color='#2a64b0'><b>"
					+getString(R.string.loading)+"</b></font>");
			text.setText(spanned);

			callerContactDetails(getShownIndex(), text);


			return scroller;
		}

		private void callerContactDetails(int id,
										  final TextView text) {
			pDialog.show();
			CallerContactDetails contactDetails = new CallerContactDetails(baseURL, TAG,
					id);
			contactDetails.setCallBackListener(new ContactDetailsListener() {
				@Override
				public void onContactDetailsAvailable(ContactDetails contactDetails) {
					if(contactDetails != null)
					{
						Spanned spanned = Html.fromHtml("<font color='#2a64b0'><b>ID: </b></font>"
								+"<font size='60' color='#4f5256'>"+contactDetails.getId()+"</b></font><small><br/><br/></small>"
								+"<font color='#2a64b0'><b>First Name: </b></font>"
								+"<font size='60' color='#4f5256'>"+contactDetails.getFirstName() +"</b></font><small><br/><br/></small>"
								+"<font color='#2a64b0'><b>Last Name: </b></font>"
								+"<font size='60' color='#4f5256'>"+contactDetails.getLastName()+"</b></font><small><br/><br/></small>"
								+"<font color='#2a64b0'><b>Age: </b></font>"
								+"<font size='60' color='#4f5256'>"+contactDetails.getAge()+"</b></font><small><br/><br/></small>"
								+"<font color='#2a64b0'><b>Favourite Colour: </b></font>"
								+"<font size='60' color='"+contactDetails.getFavouriteColour()+"'>"+contactDetails.getFavouriteColour()+"<b/></font>");
						text.setText(spanned);
					}
					pDialog.hide();
				}
			});
		}
	}

	private static void openDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(activity.getString(R.string.check_internet_connection))
				.setCancelable(false)
				.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		final AlertDialog alert = builder.create();
		// now setup to change color of the button
		alert.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				alert.getButton(AlertDialog.BUTTON_POSITIVE)
						.setTextColor(Color.parseColor("#2a64af"));
			}
		});

		try {
			alert.show();
		}catch (Exception e)
		{}
	}

}
