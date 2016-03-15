package com.allmenu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allmenu.Vote_model;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import photocontent.bliss.com.api.ConnectApi;
import photocontent.bliss.com.api.ServiceHandler;
import photocontent.bliss.com.api.ServiceHandlerUrl;
import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 5/15/2015.
 */
public class AdapterForVoteDetails extends BaseAdapter {
    private Context context;
    ArrayList<Vote_model> model_arraylist = new ArrayList<Vote_model>();
    ProgressDialog pg;
    SharedPreferences pref;
    Float dialog_rating_float;
    String reason_des_str,dialog_rating_str, dialog_title_str, userid, vote_img_final, spam_img_final, rating_value, date_str, title_str, created_by_str, spam_user_id, main_image, description_str, photomas_id;
    boolean vote_photo = false;
    boolean spam_user = false;
    TextView ok_butt,cancel_butt;
    EditText reason_des;

    ServiceHandler sh;
    TextView dialog_title;

    EditText description;
    ImageView image;
    Dialog dialog;
    RatingBar rating;
    int submit_position=0, activity_value;
    CustomalertDialog alert = new CustomalertDialog();

    VotePhoto act;
    ToptenimagesActivity topten;

    LayoutInflater inflater;

    float vote_float;

    public AdapterForVoteDetails(Context context, ArrayList<Vote_model> model_arraylist, int activity_value) {
        this.context = context;
        this.model_arraylist = model_arraylist;
        this.activity_value = activity_value;
        System.out.println("activity_valueactivity_valueactivity_value"+activity_value);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            pref = context.getSharedPreferences("Options", context.MODE_PRIVATE);
            created_by_str = pref.getString("FirstName_Key", "");
            System.out.println("created by created by" + created_by_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return model_arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return model_arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Viewholder {

        ImageView vote_image;
        RatingBar main_rating_bar;
        TextView title, ranking, date, time, spam;
        LinearLayout vote_linear;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       final Viewholder holder;
        try {
            if (convertView == null) {
                holder= new Viewholder();
                convertView = inflater.inflate(R.layout.vote_photo_adapter, parent, false);

                holder.vote_linear = (LinearLayout) convertView.findViewById(R.id.vote_photo_linearlayout);
                holder.vote_image = (ImageView) convertView.findViewById(R.id.all_image_adapter);
                holder.spam = (TextView) convertView.findViewById(R.id.spam_id);
                holder.main_rating_bar = (RatingBar) convertView.findViewById(R.id.ratingBar);


                holder.date = (TextView) convertView.findViewById(R.id.date_id);
                holder.title = (TextView) convertView.findViewById(R.id.title_id);
                holder.time = (TextView) convertView.findViewById(R.id.time_id);
                holder.ranking = (TextView) convertView.findViewById(R.id.rating_text);
                userid = pref.getString("userid_key", "");

                convertView.setTag(holder);
            } else {
                holder = (Viewholder) convertView.getTag();
            }




            holder.spam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                     final Dialog spamdialog = new Dialog(context);
                    spamdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    spamdialog.setContentView(R.layout.spam_alert);
                    spamdialog.setCancelable(true);
                    spamdialog.setCanceledOnTouchOutside(false);
                    // set the custom dialog components - text, image and

                    ok_butt= (TextView) spamdialog.findViewById(R.id.ok_butt);
                    cancel_butt= (TextView) spamdialog.findViewById(R.id.cancel_butt);
                //    reason_des=(EditText)dialog.findViewById(R.id.reason_id);

                    // if button is clicked, close the custom dialog
                    ok_butt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            spam_user = true;
                          /*  vote_photo = false;*/
                            try {
                                spam_user_id = model_arraylist.get(position).getSpamuserid();
                                System.out.println("spam userid is"+spam_user_id);
                                reason_des_str="";
                                photomas_id = model_arraylist.get(position).getPhotomaster_id();
                                spamdialog.dismiss();
                                validationSpam();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    cancel_butt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spamdialog.dismiss();
                        }
                    });

                    spamdialog.show();
                }
            });


            if (model_arraylist.get(position).getImagepath().length() > 0) {
                if (!model_arraylist.get(position).getImagepath().equalsIgnoreCase("null")) {
                    try {
                        Picasso.with(context)
                                .load(model_arraylist.get(position).getImagepath())
                                .placeholder(R.drawable.animation_loding)
                                .into(holder.vote_image);

                        main_image = model_arraylist.get(position).getImagepath();
                        date_str = model_arraylist.get(position).getDate();
                        title_str = model_arraylist.get(position).getTitle();

                        System.out.println("rating valueee"+ConvertValue(model_arraylist.get(position).getRating()));

                        holder.main_rating_bar.setRating(Float.parseFloat(ConvertValue(model_arraylist.get(position).getRating())));
                        holder.date.setText("" + model_arraylist.get(position).getDate());
                        holder.time.setText("" + model_arraylist.get(position).getTime());
                        holder.title.setText("" + model_arraylist.get(position).getTitle());
                        holder.ranking.setText("" + model_arraylist.get(position).getRanking());
                      /*  if (activity_value == 2) {
                            holder.vote_linear.setClickable(false);
                            holder.spam.setClickable(false);
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void validationSpam() {

            if (NetworkValidation.checknetConnection(context)) {

                asynTaskForVoteSpamUser();
            }
            else {
                alert.ShowAlert(context, "Check Your Internet Connection");
            }
        }



    private void showCustomDialog(int position) {

        submit_position = position;
        // Create custom dialog object
        dialog = new Dialog(context);
        // Set dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.votephoto_upload, null, false);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


        // set values for custom dialog components - text, image and button
        dialog_title = (TextView) dialog.findViewById(R.id.title_upload_edt);
        description = (EditText) dialog.findViewById(R.id.description_edt);
        image = (ImageView) dialog.findViewById(R.id.vote_snap_img);
        rating = (RatingBar) dialog.findViewById(R.id.server_ratingBar);
        try {
            dialog_title_str = model_arraylist.get(submit_position).getTitle();
            System.out.println("tttttttttttttt"+dialog_title_str);
            dialog_rating_str = model_arraylist.get(submit_position).getRating();
           // dialog_rating_float = Float.parseFloat(ConvertValue(dialog_rating_str));
            description_str=model_arraylist.get(submit_position).getDescription();
            description.setText(description_str);
            dialog_title.setText(dialog_title_str);
            dialog_title.setEnabled(false);

           /* rating.setRating(dialog_rating_float);*/
            Picasso.with(context).load(model_arraylist.get(position).getImagepath())
                    .placeholder(R.drawable.animation_loding).resize(300, 300).into(image);
            description_str = description.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                vote_float = rating;
                rating_value = String.valueOf(rating);
                System.out.println("ratingvaluehgcgsh" + rating_value);

            }
        });


        TextView submit = (TextView) dialog.findViewById(R.id.submit_btn);
        dialog.show();


        // if decline button is clicked, close the custom dialog
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                vote_photo = true;
                spam_user = false;
                try {
                    validationForUpdateVote();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String ConvertValue(String value){
        if(value==null){
            return "0";
        }else if(value.equals("")){
            return "0";
        }else if(value.equals("null")){
            return "0";
        }

        return value;

    }

    private void validationForUpdateVote() {
        try {
            if (rating_value.trim().equalsIgnoreCase("")) {
                alert.ShowAlert(context, "Select Rating");
            } else if (NetworkValidation.checknetConnection(context)) {
                try {
                    asynTaskForVoteSpamUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alert.ShowAlert(context, "Check Your Internet Connection");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void asynTaskForVoteSpamUser() {
        new behindActivity().execute();
    }


    class behindActivity extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(context);
            pg.setMessage("Loading");
            pg.setCancelable(true);
            pg.setCanceledOnTouchOutside(false);
            pg.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            if (spam_user == true) {
                try {
                    spam_img_final = ConnectApi.ResponseForSpamUser(userid, reason_des_str, photomas_id, spam_user_id);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            pg.dismiss();

            if (spam_user == true) {
                try {
                    parsethespam_userresponse(spam_img_final);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void parsethespam_userresponse(String spam_img_final) {
            try {

                JSONObject jsonobj = new JSONObject(spam_img_final);

                if (Boolean.valueOf(jsonobj.getString("status"))) {
                    Toast.makeText(context, "Spam User Marked Successfully", Toast.LENGTH_SHORT).show();
                    if(activity_value==1) {
                        VotePhoto.act.callAsynTask();
                    }
                    if(activity_value==2){
                     ToptenimagesActivity.topten.apiIntegrationForTopPhotos();
                    }
                }
                else {
                    alert.ShowAlert(context, "Spam Function is not Working for your photo");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void parsethevote_photouploadresp(String vote_img_final) {
            String msg = null;
            dialog.dismiss();
            try {

                JSONObject jsonobj = new JSONObject(vote_img_final);

                if (Boolean.valueOf(jsonobj.getString("status"))) {


                    model_arraylist.get(submit_position).setDescription(description_str);
                    model_arraylist.get(submit_position).setRating(rating_value);
                    Toast.makeText(context, "Vote Updated Successfully", Toast.LENGTH_SHORT).show();
                    VotePhoto.act.callAsynTask();

                     }
                else {

                  alert.ShowAlert(context,"Something Missing");
                    dialog.dismiss();

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}


