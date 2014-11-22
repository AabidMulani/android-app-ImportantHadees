package in.abmulani.importanthadees.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.abmulani.importanthadees.BaseActivity;
import in.abmulani.importanthadees.R;
import in.abmulani.importanthadees.database.HadeesTable;
import in.abmulani.importanthadees.utils.AppConstants;
import in.abmulani.importanthadees.utils.Utils;
import timber.log.Timber;

/**
 * Created by AABID on 22-11-2014.
 */
public class DetailViewActivity extends BaseActivity {

    @InjectView(R.id.textView_title)
    TextView titleTextView;

    @InjectView(R.id.textView_desc)
    TextView descTextView;

    @InjectView(R.id.textView_reference)
    TextView referenceTextView;

    private HadeesTable selectedHadees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_detail_view);
        ButterKnife.inject(this);
        setActionBarTitle();
        selectedHadees = (HadeesTable) getIntent().getParcelableExtra(HomeScreenActivity.EXTRA_OBJECT);
        titleTextView.setText(selectedHadees.getTitle());
        descTextView.setText(selectedHadees.getDescription());
        referenceTextView.setText(selectedHadees.getReference());
    }

    public void setActionBarTitle() {
        Spannable actionBarTitle = new SpannableString(getString(R.string.app_name));
        actionBarTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)),
                0, actionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.d("onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mark_read:
                HadeesTable.setThisAsRead(selectedHadees.getRowCount(), false);
                Utils.showToast(activity, "Done!");
                break;
            case R.id.action_share:
                String shareContent = "Hadees Topic:\n"
                        + selectedHadees.getTitle() + "\n"
                        + "-------------------------\n"
                        + "Description:\n"
                        + selectedHadees.getTitle() + "\n"
                        + "-------------------------\n"
                        + "Reference:\n"
                        + selectedHadees.getReference() + "\n\n\n"
                        + "Share Using:\n"
                        + "Important Hadees Android App\n"
                        + "Download Link:\n"
                        + AppConstants.PLAY_STORE_URL;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
            case R.id.action_share_app:
                Utils.showToast(activity, "Share This App [Not in Beta Version]");
                break;
            case R.id.action_rate_app:
                Utils.showToast(activity, "Rate Us [Not in Beta Version]");
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
