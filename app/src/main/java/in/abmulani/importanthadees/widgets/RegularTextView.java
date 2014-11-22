package in.abmulani.importanthadees.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by AABID on 16-08-2014.
 */
public class RegularTextView extends TextView {

    public RegularTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/AlteHaasGroteskRegular.ttf");
        this.setTypeface(face);
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/AlteHaasGroteskRegular.ttf");
        this.setTypeface(face);
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/AlteHaasGroteskRegular.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
