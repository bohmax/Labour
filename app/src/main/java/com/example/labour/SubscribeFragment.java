package com.example.labour;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubscribeFragment extends DialogFragment implements PopupMenu.OnMenuItemClickListener {

    private Button button;
    private CircleImageView civ;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_data);
        builder.setIcon(R.drawable.ic_account_circle_black_24dp);
        builder.setMessage("Altrimenti si può continuare a lavorare in maniera quasi anonima");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.subscribe_fragment, null);
        button = view.findViewById(R.id.buttonsex);
        civ = view.findViewById(R.id.image);
        civ.setColorFilter(getResources().getColor(R.color.grey) , PorterDuff.Mode.DARKEN);
        builder.setView(view);

        builder.setPositiveButton("Imposta",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Posso lavorarci",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Canella", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subscribe_fragment, container);
        button = v.findViewById(R.id.buttonsex);
        return v;
    }*/


    //--------------- popup & image click -----------------
    void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        final PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sesso, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        button.setText(item.getTitle());
        return true;
    }

    public void onImageClick(View v) {
        Log.i("AH","AHAH");
    }
    //--------------------------------------------
    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings :
                startActivity(new Intent(getContext(), PreferenceActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}
