package com.example.labour;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labour.interfacce.BitmapReadyListener;
import com.example.labour.interfacce.CardViewClickListener;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {

    private ArrayList<Package_item> packs;
    private CardViewClickListener cvc;
    private BitmapReadyListener brl;

    public PackAdapter(CardViewClickListener cvc, ArrayList<Package_item> packs) throws NullPointerException, ClassCastException {
        if (packs == null) throw new NullPointerException();
        this.packs = packs;
        this.cvc = cvc;
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }

    @NonNull
    @Override
    public PackViewHolder onCreateViewHolder(ViewGroup vg, int i) {
        View v = LayoutInflater.from(vg.getContext()).inflate(R.layout.card_layout, vg, false);
        PackViewHolder pv = new PackViewHolder(v);
        if (cvc != null)
            pv.setonCardViewClickListener(cvc);
        return pv;
    }

    @Override
    public void onBindViewHolder(PackViewHolder PackViewHolder, int i) {
        PackViewHolder.cv.setTag(i);
        Package_item item = packs.get(i);
        PackViewHolder.titolo.setText(item.getTitle());
        PackViewHolder.descr.setText(item.getDescription());
        if (brl == null)
            PackViewHolder.photo.setImageResource(item.getPhotoId());
        else if (item.getPhoto() != null)
            PackViewHolder.photo.setImageBitmap(item.getPhoto());
        else //percorso presente, ma bitmap assente
            brl.startImageRequest(item, i);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeLastItem(int pos) {
        packs.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, packs.size());
    }

    public void addElement(Package_item item) {
        packs.add(item);
        notifyItemInserted(packs.size() - 1);
    }

    public ArrayList<Package_item> getPacks() {
        return packs;
    }

    public void updatePassi(float coordinata){
        for (Package_item item: packs)
            item.getRoute().passo(coordinata);
    }

    public void setImageDownload(BitmapReadyListener brl){
        this.brl = brl;
    }

    public void disableImageDownload(){
        this.brl = null;
    }

    static class PackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView titolo;
        TextView descr;
        ImageView photo;
        CardViewClickListener listener;

        PackViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            titolo = itemView.findViewById(R.id.pack_title);
            descr = itemView.findViewById(R.id.pack_descr);
            photo = itemView.findViewById(R.id.pack_photo);
        }

        void setonCardViewClickListener(CardViewClickListener activity) {
            listener = activity;
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onCardViewClick((Integer) v.getTag());
        }
    }
}
