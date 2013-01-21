package com.makotu.rss.reader.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.makotu.rss.reader.parser.RssParser;
import com.makotu.rss.reader.util.LayoutUtil;
import com.makotu.rss.reader.util.ToastUtil;

public class RegistRssFeedActivity extends RssBaseActivity implements OnClickListener {

    EditText rssFeed;   //RSS�t�B�[�h�o�^�e�L�X�g
    Button  rssAddBtn, clearBtn;    //�ǉ��{�^���A�N���A�{�^��
    RadioButton oneHour, sixHour, twentyHour, oneDay;    //�X�V�Ԋu���W�I�{�^��
    RadioGroup rg;  //���W�I�{�^���O���[�v

    /**
     * RSS�o�^��ʂ̏����\��
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //��ʃ��C�A�E�g�̍쐬�A�c����
        LinearLayout dispLayout = new LinearLayout(this);
        dispLayout.setOrientation(LinearLayout.VERTICAL);

        //RSS�t�B�[�h�̃��C�A�E�g �������ɐݒ�
        LinearLayout feedLayout = new LinearLayout(this);
        feedLayout.setOrientation(LinearLayout.HORIZONTAL);

        //�{�^�����C�A�E�g �������ɐݒ�
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        setContentView(dispLayout);

        //URL�̓��͍��ڐ���
        TextView Urltv = (new TextView(this));
        Urltv.setText("URL:");

        rssFeed = new EditText(this);
        //------�f�o�b�O�p------
        rssFeed.setText("http://www.kotaro269.com/index.rdf");
        //-------------------

        //RSS�t�B�[�h���C�A�E�g�ɐݒ�
        feedLayout.addView(Urltv, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        feedLayout.addView(rssFeed, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));

        //�X�V�Ԋu�̃��W�I�{�^������
        TextView radioTv = new TextView(this);
        radioTv.setText("�X�V�Ԋu");

        //�X�V�Ԋu1���Ԗ�
        oneHour = new RadioButton(this);
        oneHour.setId(1);
        oneHour.setText("1���Ԗ�");

        //�X�V�Ԋu6���Ԗ�
        sixHour = new RadioButton(this);
        sixHour.setId(6);
        sixHour.setText("6���Ԗ�");

        //�X�V�Ԋu12���Ԗ�
        twentyHour = new RadioButton(this);
        twentyHour.setId(12);
        twentyHour.setText("12���Ԗ�");

        //�X�V�Ԋu24���Ԗ�
        oneDay = new RadioButton(this);
        oneDay.setId(24);
        oneDay.setText("24���Ԗ�");

        //���W�I�O���[�v�̍쐬�A���W�I�{�^���ǉ�
        rg = new RadioGroup(this);
        rg.addView(oneHour);
        rg.addView(sixHour);
        rg.addView(twentyHour);
        rg.addView(oneDay);
        rg.check(1);
        rg.setLayoutParams(LayoutUtil.getLayoutParamsWrap());

        //�ǉ��{�^���̍쐬
        rssAddBtn = new Button(this);
        rssAddBtn.setText("�t�B�[�h�̒ǉ�");
        rssAddBtn.setOnClickListener(this);

        //�N���A�{�^���̍쐬
        clearBtn = new Button(this);
        clearBtn.setText("�N���A");
        clearBtn.setOnClickListener(this);

        //�{�^�����{�^�����C�A�E�g�ɒǉ�
        btnLayout.addView(rssAddBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        btnLayout.addView(clearBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));

        //���C�A�E�g�ƃ��W�I�{�^������ʃ��C�A�E�g�ɒǉ�
        dispLayout.addView(feedLayout, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));
        dispLayout.addView(radioTv);
        dispLayout.addView(rg);
        dispLayout.addView(btnLayout, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));
    }

    /**
     * �ǉ��A�N���A�{�^���N���b�N���̃C�x���g���X�i�[
     * @param   view    �N���b�N���ꂽ�{�^���I�u�W�F�N�g
     */
    public void onClick(View view) {
        // RSS�t�B�[�h�ǉ��{�^���N���b�N��
        if (view == rssAddBtn) {
            String rssUrl = rssFeed.getText().toString();

            //ID�̏����l
            int id = -1;
            //RSS�t�B�[�h�̍X�V�Ԋu���擾
            int updHour = rg.getCheckedRadioButtonId();

            //RSS�t�B�[�h�̃w�b�_����DB�֊i�[
            if ((id = RssParser.parseRssFeed(rssUrl, updHour)) >= 0) {
                ToastUtil.showToastLong(this, "RSS�t�B�[�h�̓o�^�ɐ������܂����B�R���e���c���擾���Ă��܂��B");

                //RSS�̋L����ǂݍ��݃f�[�^�x�[�X�֓o�^
                RssParser.parseRssContents(rssUrl, String.valueOf(id));
                ToastUtil.showToastShort(this, "RSS�R���e���c���擾���܂����B");

                //��ʂ̖߂�l��ݒ�
                setResult(RESULT_OK);
                //��ʂ̏I��
                back();
            } else {
                ToastUtil.showToastShort(this, "RSS�t�B�[�h�̓o�^�Ɏ��s���܂����B���łɓo�^����Ă��邩�ARSS2.0�ɑΉ����Ă��邩�m�F���Ă�������");
            }
        } else if (view == clearBtn) {
            //RSS�t�B�[�h�̒l���N���A
            rssFeed.setText("");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        setResult(RESULT_OK);
        return super.dispatchKeyEvent(event);
    }

}
