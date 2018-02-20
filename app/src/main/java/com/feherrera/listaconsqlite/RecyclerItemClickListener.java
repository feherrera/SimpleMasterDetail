package com.feherrera.listaconsqlite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Felipe on 12-02-2018.
 * Class that capture click and long press over a RecyclerView,
 * and provides an interface to define the behaviour: OnItemClickListener
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    protected OnItemClickListener listener;
    private GestureDetector gestureDetector;
    private ClickableAdapter clickableAdapter;

    private View childView;
    private int childViewPosition;

    /**
     * Create a listener to handle clicks on RecyclerView's Item
     * @param context The application's context
     * @param listener Own OnItemClickListener implementation
     */
    public  RecyclerItemClickListener(Context context, OnItemClickListener listener){
        this(context, listener, null);
    }

    /**
     * Create a listener to handle clicks on RecyclerView's Item
     * @param context The application's context
     * @param listener Own OnItemClickListener implementation
     * @param clickableAdapter Own ClickableAdapter implementation
     */
    public  RecyclerItemClickListener(Context context
            , OnItemClickListener listener
            , ClickableAdapter clickableAdapter){

        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.listener = listener;
        this.clickableAdapter = clickableAdapter;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        childView = rv.findChildViewUnder(e.getX(), e.getY());
        childViewPosition = rv.getChildAdapterPosition(childView);
        return childView != null && gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * Interface definition for a callback to be invoked when a RecyclerViewItem is clicked or
     * long pressed. Used to notify the adapter the selected item view.
     */
    public interface ClickableAdapter{

        /**
         * Called when an Item is clicked
         * @param position Adapter's position of the view
         */
        public void onItemSelected(View childview, int position);

        /**
         * Called when an Item is deleted.
         */
        public void resetSelected();
    }

    //region GestureListener
    /**
     * Interface definition for a callback to be invoked when a RecyclerViewItem is clicked or
     * long pressed.
     */
    public interface OnItemClickListener{

        /**
         * Called when an Item is clicked
         * @param childView ItemView
         * @param position Adapter's position of the view
         */
        public void onItemClick(View childView, int position);

        /**
         * Called when an Item is long-pressed
         * @param childView ItemView
         * @param position Adapter's position of the view
         */
        public  void onItemLongPress(View childView, int position);
    }

    /**
     * Class that implement needed methods for Gesture handle.
     */
    private class GestureListener extends  GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent event){
            if(childView != null){
                listener.onItemClick(childView,childViewPosition);

                if (clickableAdapter != null){
                    clickableAdapter.onItemSelected(childView, childViewPosition);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event){
            if(childView != null){
                listener.onItemLongPress(childView, childViewPosition);
                clickableAdapter.resetSelected();
            }
        }

        @Override
        public boolean onDown(MotionEvent event){
            return true;
        }
    }
    //endregion GestureListener
}
