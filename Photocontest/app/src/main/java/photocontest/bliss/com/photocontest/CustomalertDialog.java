package photocontest.bliss.com.photocontest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jenifa Mary.C on 5/7/2015.
 */
public class CustomalertDialog {


    public void ShowAlert(Context context,String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alertDialog.show();

    }



    public void customalert(Context context,String message,Boolean status){
    // Create custom dialog object
    final Dialog dialog = new Dialog(context);
    // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.validationcustomalert);

    // set values for custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.custom_text);

        text.setText(message);

    TextView done = (TextView) dialog.findViewById(R.id.done_id);
    dialog.show();

        if(status != null)
    // if decline button is clicked, close the custom dialog
        done.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Close dialog
            dialog.dismiss();
        }
    });
    }
}
