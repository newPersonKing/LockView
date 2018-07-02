package com.andrognito.patternlockdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompleteEvent;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private PatternLockView mPatternLockView;

    Gson gson;

    Button btn;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));

            if (SPUtils.getInstance().get("HAND_KEY","AAA")!="AAA"){
                String keyGson= (String) SPUtils.getInstance().get("HAND_KEY","AAA");
                MySavePattern savePattern=gson.fromJson(keyGson,MySavePattern.class);
                match(savePattern.getOlddots(),pattern);
                mPatternLockView.clearPattern();
                mPatternLockView.invalidate();
                return;
            }
            /*第一次绘制完成保存绘制的点*/
            if (MyPattern.getOlddots().size()>0){
                MyPattern.setNewdots(pattern);
                match(MyPattern.getOlddots(),MyPattern.getNewdots());
            }else {

                MyPattern.setOlddots(pattern);
                btn.setText("请再次绘制");
            }
            mPatternLockView.clearPattern();
            mPatternLockView.invalidate();
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };
    MySavePattern MyPattern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        btn= (Button) findViewById(R.id.match);
        gson=new Gson();

        MyPattern=new MySavePattern();


        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);

        RxPatternLockView.patternComplete(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompleteEvent>() {
                    @Override
                    public void accept(PatternLockCompleteEvent patternLockCompleteEvent) throws Exception {
                        Log.d(getClass().getName(), "Complete: " + patternLockCompleteEvent.getPattern().toString());
                    }
                });

        RxPatternLockView.patternChanges(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                            Log.d(getClass().getName(), "Pattern drawing started");
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            Log.d(getClass().getName(), "Pattern progress: " +
                                    PatternLockUtils.patternToString(mPatternLockView, event.getPattern()));
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            Log.d(getClass().getName(), "Pattern complete: " +
                                    PatternLockUtils.patternToString(mPatternLockView, event.getPattern()));
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_CLEARED) {
                            Log.d(getClass().getName(), "Pattern has been cleared");
                        }
                    }
                });

    }

    private void match(List<PatternLockView.Dot> olddots, List<PatternLockView.Dot> newdots){
        if (olddots.size()!=newdots.size()){
            newdots.clear();
            olddots.clear();
            Toast.makeText(MainActivity.this,"匹配失败",Toast.LENGTH_SHORT).show();
            btn.setText("绘制手势锁");
            return;
        }
        for (int i=0;i<MyPattern.getOlddots().size();i++){
            int oldRow=olddots.get(i).getRow();
            int oldColum=olddots.get(i).getColumn();
            int newRow=newdots.get(i).getRow();
            int newColum=newdots.get(i).getColumn();

            if (oldColum!=newColum||oldRow!=newRow){
                MyPattern.getOlddots().clear();
                MyPattern.getNewdots().clear();
                Toast.makeText(MainActivity.this,"匹配失败",Toast.LENGTH_SHORT).show();
                btn.setText("绘制手势锁");
                return;
            }
        }
        String gsonStr=gson.toJson(MyPattern);
        SPUtils.getInstance().set("HAND_KEY",gsonStr);
        Toast.makeText(MainActivity.this,"匹配成功",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,SecondActicity.class);
        startActivity(intent);
    }
}
