package com.muthuraj.soundprofilechanger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Created by Muthuraj on 18/11/17.
 * <p>
 * Jambav, Zoho Corporation
 */

public final class ModeSelectionAdapter extends RecyclerView.Adapter<ModeSelectionAdapter.ModeItemViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    final String[] dataSet = new String[4];

    ModeSelectionAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

        for (int i = 0; i < 4; i++) {
            dataSet[i] = PreferenceUtil.getModeLabelForIndex(context, i);
        }
    }

    @Override
    public ModeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModeItemViewHolder(
                inflater.inflate(R.layout.audio_mode_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ModeItemViewHolder holder, int position) {
        PreferenceUtil.putIndex(context, dataSet[position], position);
        holder.bindData(context, dataSet[position]);
    }

    @Override
    public void onViewRecycled(ModeItemViewHolder holder) {
        super.onViewRecycled(holder);
        holder.modeCheckBox.setOnCheckedChangeListener(null);
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    static class ModeItemViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox modeCheckBox;
        private final TextView modeLabelTextView;

        ModeItemViewHolder(View itemView) {
            super(itemView);
            modeCheckBox = itemView.findViewById(R.id.mode_check_box);
            modeLabelTextView = itemView.findViewById(R.id.mode_label);
        }

        private void bindData(final Context context, final String modeLabel) {

            modeCheckBox.setChecked(PreferenceUtil.isModeEnabled(context, modeLabel));
            modeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    PreferenceUtil.setModeEnabled(buttonView.getContext(), modeLabel, isChecked);
                }
            });

            switch (modeLabel) {
                case PreferenceUtil.MODE_NORMAL: {
                    modeLabelTextView.setText(R.string.sound);
                    break;
                }
                case PreferenceUtil.MODE_SILENT: {
                    modeLabelTextView.setText(R.string.silent);
                    break;
                }
                case PreferenceUtil.MODE_VIBRATE: {
                    modeLabelTextView.setText(R.string.vibrate);
                    break;
                }
                case PreferenceUtil.MODE_NO_MEDIA: {
                    modeLabelTextView.setText(R.string.music_off);
                    break;
                }
            }
        }
    }
}

class ModeSelectionMoveCallback extends ItemTouchHelper.SimpleCallback {

    ModeSelectionMoveCallback() {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN | ItemTouchHelper.UP);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        ModeSelectionAdapter adapter = (ModeSelectionAdapter) recyclerView.getAdapter();
        int oldPosition = viewHolder.getAdapterPosition();
        int newPosition = target.getAdapterPosition();

        //swap value in dataSet from oldPosition to newPosition
        String oldValue = adapter.dataSet[oldPosition];
        String newValue = adapter.dataSet[newPosition];
        adapter.dataSet[newPosition] = oldValue;
        adapter.dataSet[oldPosition] = newValue;

        //to move item with default animation
        adapter.notifyItemMoved(oldPosition, newPosition);

        //on moving, the index of all items from top most item changed should be invalidated and
        //updated again
        int minValue = Math.min(oldPosition, newPosition);
        adapter.notifyItemRangeChanged(minValue, adapter.dataSet.length - minValue);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
