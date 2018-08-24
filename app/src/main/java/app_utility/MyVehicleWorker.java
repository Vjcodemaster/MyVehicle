package app_utility;

import android.support.annotation.NonNull;

import androidx.work.Worker;

public class MyVehicleWorker extends Worker {
    @NonNull
    @Override
    public Result doWork() {

        return Result.SUCCESS;
    }
}
