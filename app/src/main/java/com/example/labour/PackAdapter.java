    package com.example.labour;

    import android.app.Activity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.cardview.widget.CardView;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.labour.interfacce.CardViewClickListener;

    import java.util.List;

    public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder>{
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

                    cv.setOnClickListener(this);
                }

                void setonCardViewClickListener(CardViewClickListener activity){
                    listener = activity;
                }

                @Override
                public void onClick(View v) {
                    listener.onCardViewClick((Integer) v.getTag());
                }



            }

            private List<Package_item> packs;
            private CardViewClickListener act;

            public PackAdapter(Activity act, List<Package_item> packs) throws NullPointerException, ClassCastException{
                if(act == null || packs == null) throw new NullPointerException();
                if (!(act instanceof CardViewClickListener)) throw new ClassCastException();
                this.packs = packs;
                this.act = (CardViewClickListener) act;
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
                pv.setonCardViewClickListener(act);
                return pv;
            }

            @Override
            public void onBindViewHolder(PackViewHolder PackViewHolder, int i) {
                PackViewHolder.cv.setTag(i);
                Package_item item = packs.get(i);
                PackViewHolder.titolo.setText(item.getTitle());
                PackViewHolder.descr.setText(item.getDescription());
                PackViewHolder.photo.setImageResource(item.getPhotoId());
            }

            @Override
            public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
            }

            public void removeLastItem(int pos){
                packs.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, packs.size());
            }

            public void addElement(Package_item item){
                packs.add(item);
                notifyItemInserted(packs.size() - 1);
            }


        }
