//package com.example.vpa;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//public class PinLock extends Activity {
//	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
//	public PinLock() {
//	
//	}
//	public void pinlock_code(boolean first_pin,Context context,final String pin){
//		AlertDialog.Builder alert = new AlertDialog.Builder(context);
//		alert.setTitle(R.string.pin_code);
//		final EditText et = new EditText(context);
//		final EditText et2 = new EditText(context);
//		et.setHint(R.string.enter_current_pin_code);
//		et2.setHint(R.string.enter_new_pin_code);
//		if (first_pin)
//			alert.setView(et);
//		else if (!first_pin) {
//			LinearLayout ll = new LinearLayout(context);
//			ll.setOrientation(LinearLayout.VERTICAL);
//			ll.addView(et);
//			ll.addView(et2);
//			alert.setView(ll);
//		}
//		alert.setPositiveButton(R.string.set,
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if (et.getText().toString().equals(pin)) {
//							pin = et2.getText().toString();
//							Toast.makeText(getApplicationContext(),
//									"PIN changed!", Toast.LENGTH_LONG).show();
//						} else
//							Toast.makeText(getApplicationContext(),
//									"Wrong current PIN", Toast.LENGTH_LONG).show();
//					}
//				});
//		alert.show();
//
//	}
//	private final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (ACTION.equals(action)) 
//            {
//                //your SMS processing code
////                displayAlert();
//            }
//        }
//	}
//}
