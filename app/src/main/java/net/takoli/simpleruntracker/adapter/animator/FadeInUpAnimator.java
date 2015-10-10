package net.takoli.simpleruntracker.adapter.animator;

import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.adapter.RunAdapter;


public class FadeInUpAnimator extends BaseItemAnimator {

    private RunAdapter adapter;
    private int green;
    private int blue;
    private TimeInterpolator interp;
    private int lastAddedPos;

    public FadeInUpAnimator(RunAdapter adapter, Resources res) {
        this.adapter = adapter;
        green = res.getColor(R.color.green_light);
        blue = res.getColor(R.color.one_run_color);
        interp = new AccelerateDecelerateInterpolator();
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(holder.itemView.getHeight() * .75f)
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        lastAddedPos = adapter.getLastAddedPosition();
        if (lastAddedPos != -1 && holder.getAdapterPosition() != lastAddedPos)
            return;
        ViewCompat.setTranslationY(holder.itemView, holder.itemView.getHeight() * .75f);
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        if (lastAddedPos != -1 && holder.getAdapterPosition() != lastAddedPos)
            return;
        else
            adapter.invalidateLastAddedPosition();
        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder))
                .start();
        final View container = holder.itemView.findViewById(R.id.one_run_container);
        if (container != null) {
            final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), green, blue);
            colorAnimation.setDuration(2000);
            colorAnimation.setInterpolator(interp);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    container.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
    }
}