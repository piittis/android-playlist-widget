package com.wavy.spotifyplaylistwidget.listAdapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * Handles callback from an ItemtouchHelper that will be attached to the arrange list.
 * ItemTouchHelper handles item dragging.
 */
public class ArrangeTouchHelperCallback extends ItemTouchHelper.Callback {

    public class OnMoveArgs {
        public int from;
        public int to;

        public OnMoveArgs(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    private Observable<OnMoveArgs> onMoveObservable;
    private ObservableEmitter<OnMoveArgs> mOnMoveEmitter;

    public ArrangeTouchHelperCallback() {
        onMoveObservable = Observable.create(emitter -> mOnMoveEmitter = emitter);
    }

    public Observable<OnMoveArgs> getItemMoves() {
        return onMoveObservable;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        // Item was dragged from one position to another, update underlying data structure and notify adapter
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        mOnMoveEmitter.onNext(new OnMoveArgs(from, to));

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
