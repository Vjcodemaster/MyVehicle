package dialogs;

import android.app.Activity;
import android.view.View;
import android.widget.PopupWindow;

import com.autochip.myvehicle.R;

public class InsurancePopUpWindow {
    private Activity aActivity;
    public PopupWindow mPopUpWindowFilter;


    public InsurancePopUpWindow(Activity activity) {
        this.aActivity = activity;
        onCreateView();
    }

    private void onCreateView() {
        View mInsuranceMenuLayout = View.inflate(aActivity, R.layout.dialog_insurance, null);
    }

}
