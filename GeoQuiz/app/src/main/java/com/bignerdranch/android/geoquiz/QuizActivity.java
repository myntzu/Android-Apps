//I'm not proud of this app but I really tried my best :<

package com.bignerdranch.android.geoquiz;

import android.os.CountDownTimer; //special feature but couldn't manage to work on it in time
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.app.Activity;


public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_SCORES = "score";
    private static final String KEY_CHEATED = "cheated";
    private static final String KEY_ANSWERED = "answered";

    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int QUESTION_SCORES = 5; //allocate scores to questions
    private long mTimeLeft = 60;
    private int mCheatTokens = 3;
    private int count = 0;
    private int scores = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mTokensTextView;
    private TextView mTimerTextView;
    private Button mResetButton;
    private Button mResultSummary;
    private CountDownTimer mCountDownTimer;


    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int[] mCheckArray = {0, 0, 0, 0, 0, 0}; //to check if user answered the questions
    private int[] mScore = new int[mQuestionBank.length]; //to store scores based on num of questions
    private boolean[] mQuestionsAnswered = new boolean[mQuestionBank.length];
    private boolean mIsCheater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATED, false);
            mQuestionsAnswered = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            mScore = savedInstanceState.getIntArray(KEY_SCORES);

        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                startTimer();
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the current index in the array is 1=this means
                //question has been answered and therefore, both buttons are
                //disabled to prevent repetitive answers

                mCheckArray[mCurrentIndex] = 1;
                mFalseButton.setEnabled(false);
                mTrueButton.setEnabled(false);
                checkAnswer(true);

            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //avoiding repetitive answers when the answer
                //for false button is true
                mCheckArray[mCurrentIndex] = 1;
                mFalseButton.setEnabled(false);
                mTrueButton.setEnabled(false);
                checkAnswer(false);

            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;

                //when index in the array is unanswered (0 for false)
                //then the buttons are by default enabled
                if (mCheckArray[mCurrentIndex] == 0) {
                    mTrueButton.setEnabled(true);
                    mFalseButton.setEnabled(true);
                    updateQuestion();
                }

                //this loop is a constraint to prevent user from constantly
                //clicking on the next/prev buttons to keep on resetting
                //the previous answered entered by clicking on the button
                else{
                    for (int j = 0; j < mQuestionBank.length; j++) {
                        count += mCheckArray[j];
                    }
                    updateQuestion();
                }

                mIsCheater = false;
                updateQuestion();
            }
        });

        //previous button to go back to prev question
        mPrevButton = (ImageButton) findViewById(R.id.back_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                if (mCurrentIndex < 0) {
                    mCurrentIndex = 0;
                }
                updateQuestion();

            }
        });

        //display tokens *limited cheats*
        mTokensTextView = (TextView) findViewById(R.id.tokens_text_view);

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //given that number of cheat tokens left is more than 0
                //user is granted permission to request a cheat
                if (mCheatTokens > 0) {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                } else {
                    //cheat button is disabled once user
                    //uses up all tokens
                    mCheatButton.setEnabled(false);
                }
            }
        });

//  the dummy page appears before the quiz, didn't have enough time to work on this:
//
//        mResultSummary = (Button) findViewById(R.id.resultSummary);
//            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//            startActivity(intent);
//
       updateQuestion();


        mResetButton = (Button) findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                scores = 0;
                mCurrentIndex = 0;
                mCheatTokens = 3;
                mTokensTextView.setText("You have " + mCheatTokens + " tokens left!");

                totalScore();
                updateQuestion();
                resetQuiz();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheatTokens--;
            mTokensTextView.setText("You have " + mCheatTokens + " tokens left!");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEATED, mIsCheater);
        savedInstanceState.putBooleanArray(KEY_ANSWERED, mQuestionsAnswered);
        savedInstanceState.putIntArray(KEY_SCORES, mScore);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        //checks if question is answered:
        mQuestionsAnswered[mCurrentIndex] = true;

        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgement_toast;
        } else {
            boolean correctAns = userPressedTrue == mQuestionBank[mCurrentIndex].isAnswerTrue();
            if (correctAns) {
                messageResId = R.string.correct_toast;


            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    //didn't have enough time to work on this properly
    private void startTimer(){
        mTimerTextView = (TextView) findViewById(R.id.quiz_timer);
        mTimerTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startTimer();
            }
        });

        mCountDownTimer = new CountDownTimer(mTimeLeft, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    //resets quiz and displays prev quiz scores when user clicks on "Play Again" button on app
   private void resetQuiz() {
        for (int i = 0; i < mCheckArray.length; i++){
            mCheckArray[i] = 0;
            mCheatTokens = 3;
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }

    }

    //display total score at the end of quiz
    private void totalScore() {
        scores = QUESTION_SCORES * 100 / mQuestionBank.length;
        Toast.makeText(this, "Score:" + scores + "%!", Toast.LENGTH_SHORT).show();
    }

}