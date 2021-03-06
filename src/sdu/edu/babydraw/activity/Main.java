package sdu.edu.babydraw.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sdu.edu.babydraw.interfaces.EditTextDialogListener;
import sdu.edu.babydraw.interfaces.OnClickOkListener;
import sdu.edu.babydraw.interfaces.PaintViewCallBack;
import sdu.edu.babydraw.utils.BitMapUtils;
import sdu.edu.babydraw.utils.ImageButtonTools;
import sdu.edu.babydraw.utils.NetworkConnection;
import sdu.edu.babydraw.utils.PaintConstants;
import sdu.edu.babydraw.utils.PaintConstants.ERASER_SIZE;
import sdu.edu.babydraw.utils.PaintConstants.PEN_SIZE;
import sdu.edu.babydraw.utils.PaintConstants.PEN_TYPE;
import sdu.edu.babydraw.utils.PaintConstants.SHAP;
import sdu.edu.babydraw.view.ColorPickerDialog;
import sdu.edu.babydraw.view.ColorPickerDialog.OnColorChangedListener;
import sdu.edu.babydraw.view.OkCancleDialog;
import sdu.edu.babydraw.view.OkDialog;
import sdu.edu.babydraw.view.PaintView;
import sdu.edu.babydraw.view.SaveDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Main Activity实现了主场景的Activity主要负责PaintView与各组件的协调
 * 
 * 
 * @author hzy，lhy
 * 
 */
public class Main extends Activity implements OnClickListener {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	// PaintView
	private PaintView mPaintView = null;

	private NetworkConnection netConn = null;

	// button 界面上的各个按钮
	private ImageButton decomButton = null;
	private ImageButton voiceButton = null;
	private ImageButton saveButton = null;
	private ImageButton loadButton = null;
	private ImageButton clearButton = null;
	private ImageButton eraserButton = null;
	private ImageButton colorSelectButton = null;
	private ImageButton penSizeButton = null;
	private ImageButton undoButton = null;
	private ImageButton redoButton = null;
	private ImageButton toButtonLayoutButton = null;
	private ImageButton toColorLayoutButton = null;
	// private ImageButton toolButton = null;

	// 通过控制Layout来控制某些变化
	private LinearLayout colorLayout = null;
	private LinearLayout buttonLayout = null;
	private LinearLayout paintViewLayout = null;
	private LinearLayout eraserSizeLayout = null;
	private LinearLayout penSizeLayout = null;
	private LinearLayout shapLayout = null;
	private LinearLayout shapLayoutf = null;

	// 一些RadioGroup和重要的(也就是默认的)RadioButton
	private RadioGroup colorRadioGroup = null;
	private RadioGroup eraserSizeRadioGroup = null;
	private RadioButton eraserSizeRadio01 = null;
	private RadioGroup penSizeRadioGroup = null;
	private RadioButton penSizeRadio1 = null;
	private RadioGroup shapRadioGroup = null;
	private RadioGroup shapRadioGroupf = null;
	private RadioButton curvRadioButton = null;

	// 用于两个SizeRadioGroup的一些操作
	private boolean clearCheckf = false;
	private boolean clearCheck = false;
	@SuppressWarnings("unused")
	private boolean checkShowPath = false;

	// 使用PenType临时存储选择的变量，当创建时再传给PaintView
	private int mPenType = PEN_TYPE.PLAIN_PEN;

	@SuppressLint("HandlerLeak")
	/**
	 * handler处理消息队列
	 */
	private Handler mHandler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.arg1 == 1) {
				Main.this.onClickButtonUndo();
			}
			if (msg.arg1 == 2) {
				Main.this.onClickButtonRedo();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		initLayout();
		initButtons();
		initPaintView();
		// initPopUpWindow();
		initCallBack();
		initShapRadioGroups();
	}

	/**
	 * 初始化所有的RadioGroup
	 */
	private void initShapRadioGroups() {
		shapRadioGroup = (RadioGroup) findViewById(R.id.shapRadioGroup);
		curvRadioButton = (RadioButton) findViewById(R.id.RadioButtonShapCurv);
		shapRadioGroupf = (RadioGroup) findViewById(R.id.shapRadioGroupf);
		initEraseSizeGroup();
		initPenSizeGroup();
		initShapRadioGroup();
		initShapRadioGroupf();
	}

	/**
	 * 初始化画笔实心样式
	 */
	private void initShapRadioGroupf() {
		shapRadioGroupf
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 如果当前没有选中
						if (checkedId == -1) {
							return;
						}
						if (clearCheck == false) {
							clearCheckf = true;
							shapRadioGroup.clearCheck();
						}
						mPaintView.setPenStyle(Paint.Style.FILL);
						switch (checkedId) {
						case R.id.RadioButtonShapLine:
							mPaintView.setCurrentShapType(SHAP.LINE);
							mPaintView.setPenStyle(Paint.Style.STROKE);
							break;
						case R.id.RadioButtonShapRectf:
							mPaintView.setCurrentShapType(SHAP.RECT);
							break;
						case R.id.RadioButtonShapCirclef:
							mPaintView.setCurrentShapType(SHAP.CIRCLE);
							break;
						case R.id.RadioButtonShapOvalf:
							mPaintView.setCurrentShapType(SHAP.OVAL);
							break;
						case R.id.RadioButtonShapSquaref:
							mPaintView.setCurrentShapType(SHAP.SQUARE);
							break;
						default:
							break;
						}
						clearCheckf = false;
					}
				});
	}

	/**
	 * 初始化画笔空心样式
	 */
	private void initShapRadioGroup() {
		curvRadioButton.setChecked(true);
		shapRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 如果当前没有选中
						if (checkedId == -1) {
							return;
						}
						/**
						 * 不需要每次都调用
						 */
						if (clearCheckf == false) {
							clearCheck = true;
							shapRadioGroupf.clearCheck();
						}
						mPaintView.setPenStyle(Paint.Style.STROKE);
						switch (checkedId) {
						case R.id.RadioButtonShapCurv:
							mPaintView.setCurrentShapType(SHAP.CURV);
							break;
						case R.id.RadioButtonShapRect:
							mPaintView.setCurrentShapType(SHAP.RECT);
							break;
						case R.id.RadioButtonShapCircle:
							mPaintView.setCurrentShapType(SHAP.CIRCLE);
							break;
						case R.id.RadioButtonShapOval:
							mPaintView.setCurrentShapType(SHAP.OVAL);
							break;
						case R.id.RadioButtonShapSquare:
							mPaintView.setCurrentShapType(SHAP.SQUARE);
							break;
						default:
							break;
						}
						clearCheck = false;
					}
				});
	}

	/**
	 * 初始化Pensize的radioGroup,画笔尺寸大小
	 */
	private void initPenSizeGroup() {
		penSizeRadioGroup = (RadioGroup) findViewById(R.id.penRaidoGroup);
		penSizeRadio1 = (RadioButton) findViewById(R.id.RadioButtonPen01);
		penSizeRadio1.setChecked(true);
		penSizeRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.RadioButtonPen01:
							mPaintView.setPenSize(PEN_SIZE.SIZE_1);
							break;
						case R.id.RadioButtonPen02:
							mPaintView.setPenSize(PEN_SIZE.SIZE_2);
							break;
						case R.id.RadioButtonPen03:
							mPaintView.setPenSize(PEN_SIZE.SIZE_3);
							break;
						case R.id.RadioButtonPen04:
							mPaintView.setPenSize(PEN_SIZE.SIZE_4);
							break;
						case R.id.RadioButtonPen05:
							mPaintView.setPenSize(PEN_SIZE.SIZE_5);
							break;
						default:
							break;
						}
					}
				});
	}

	/**
	 * 初始化EraseSize，橡皮尺寸
	 */
	private void initEraseSizeGroup() {
		eraserSizeRadioGroup = (RadioGroup) findViewById(R.id.eraseRaidoGroup);
		eraserSizeRadio01 = (RadioButton) findViewById(R.id.RadioButtonEraser01);
		eraserSizeRadio01.setChecked(true);
		eraserSizeRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.RadioButtonEraser01:
							mPaintView.setEraserSize(ERASER_SIZE.SIZE_1);
							break;
						case R.id.RadioButtonEraser02:
							mPaintView.setEraserSize(ERASER_SIZE.SIZE_2);
							break;
						case R.id.RadioButtonEraser03:
							mPaintView.setEraserSize(ERASER_SIZE.SIZE_3);
							break;
						case R.id.RadioButtonEraser04:
							mPaintView.setEraserSize(ERASER_SIZE.SIZE_4);
							break;
						case R.id.RadioButtonEraser05:
							mPaintView.setEraserSize(ERASER_SIZE.SIZE_5);
							break;
						default:
							break;
						}
					}
				});
	}

	/**
	 * 初始化paintView的回调函数
	 */
	private void initCallBack() {
		mPaintView.setCallBack(new PaintViewCallBack() {
			// 当画了之后对Button进行更新
			@Override
			public void onHasDraw() {
				enableUndoButton();
				disableRedoButton();
			}

			// 当点击之后让各个弹出的窗口都消失
			@Override
			public void onTouchDown() {
				setAllLayoutInvisable();
			}
		});
	}

	/**
	 * 初始化画画所用的paintView
	 */
	private void initPaintView() {
		mPaintView = new PaintView(this);
		paintViewLayout.addView(mPaintView);
	}

	/**
	 * 初始化所用到的Layout
	 */
	private void initLayout() {
		colorLayout = (LinearLayout) findViewById(R.id.LinearLayoutColor);
		buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
		colorRadioGroup = (RadioGroup) findViewById(R.id.radioGroupColor);
		paintViewLayout = (LinearLayout) findViewById(R.id.paintViewLayout);
		eraserSizeLayout = (LinearLayout) findViewById(R.id.sizeSelectLayout);
		penSizeLayout = (LinearLayout) findViewById(R.id.sizeSelectLayoutPen);
		eraserSizeLayout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popbackground4));
		penSizeLayout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popbackground4));
		shapLayout = (LinearLayout) findViewById(R.id.shapSelectLayout1);
		shapLayoutf = (LinearLayout) findViewById(R.id.shapSelectLayout2);
	}

	/**
	 * 如果是Eraser则Set到上一个PenType
	 */
	private void setToLastPenTeype() {
		if (mPaintView.getCurrentPainter() == PEN_TYPE.ERASER) {
			mPaintView.setCurrentPainterType(mPenType);
		}
	}

	/**
	 * 初始化所有的Button
	 */
	private void initButtons() {
		findButtonById();
		setBackGroundDrawable();
		List<ImageButton> list = initButtonList();
		for (ImageButton imageButton : list) {
			ImageButtonTools.setButtonFocusChanged(imageButton);
			imageButton.setOnClickListener(this);
		}
		voiceButton.setOnClickListener(this);
	}

	/**
	 * 将需要处理的ImageButton加入到List中
	 */
	private List<ImageButton> initButtonList() {
		List<ImageButton> list = new ArrayList<ImageButton>();
		list.add(loadButton);
		list.add(clearButton);
		list.add(eraserButton);
		list.add(saveButton);
		list.add(penSizeButton);
		list.add(colorSelectButton);
		list.add(undoButton);
		list.add(redoButton);
		list.add(toButtonLayoutButton);
		list.add(toColorLayoutButton);
		// list.add(toolButton);
		list.add(voiceButton);
		list.add(decomButton);
		return list;
	}

	/**
	 * 找到所有的通过所有的button
	 */
	private void findButtonById() {
		decomButton = (ImageButton) findViewById(R.id.buttonDecom);
		voiceButton = (ImageButton) findViewById(R.id.buttonVoice);
		saveButton = (ImageButton) findViewById(R.id.imageButtonSave);
		loadButton = (ImageButton) findViewById(R.id.imageButtonLoadPicture);
		clearButton = (ImageButton) findViewById(R.id.imageButtonClear);
		eraserButton = (ImageButton) findViewById(R.id.imageButtonEraser);
		penSizeButton = (ImageButton) findViewById(R.id.imageButtonPen);
		colorSelectButton = (ImageButton) findViewById(R.id.imageButtonColorSelect);

		undoButton = (ImageButton) findViewById(R.id.imageButtonUndo);
		redoButton = (ImageButton) findViewById(R.id.imageButtonRedo);

		toButtonLayoutButton = (ImageButton) findViewById(R.id.imageButtonToButtonLayout);
		toColorLayoutButton = (ImageButton) findViewById(R.id.imageButtonToColorLayout);
	}

	/**
	 * 初始化所有Button的Drawable
	 */
	private void setBackGroundDrawable() {

		voiceButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.voice));
		decomButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.decomposition));
		clearButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.newfile));
		eraserButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.eraser));
		loadButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.loadpicture));
		saveButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.save));
		penSizeButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.pen_default));

		redoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantredo));
		undoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantundo));
		toButtonLayoutButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.changetobuttonlayout));
		toColorLayoutButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.changetocolorlayout));

	}

	/**
	 * onClick函数
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonVoice:
			netConn = new NetworkConnection(this);
			if (!netConn.checkNetworkStates()) {
				Toast.makeText(Main.this, "网络连接失败，请检查网络", Toast.LENGTH_LONG)
						.show();
			} else {
				this.startVoiceRecognitionActivity();
			}
			break;

		case R.id.imageButtonSave:
			onClickButtonSave();
			break;

		case R.id.imageButtonLoadPicture:
			onClickButtonLoad();
			break;

		case R.id.imageButtonClear:
			onClickButtonClear();
			break;

		case R.id.imageButtonEraser:
			onClickButtonEraser();
			break;

		case R.id.imageButtonPen:
			onCLickButtonPen();
			break;

		case R.id.imageButtonColorSelect:
			onClickButtonColorSelect();
			break;

		case R.id.imageButtonUndo:
			onClickButtonUndo();
			break;

		case R.id.imageButtonRedo:
			onClickButtonRedo();
			break;

		case R.id.imageButtonToColorLayout:
			onClickButtonColorSelect();
			break;

		case R.id.imageButtonToButtonLayout:
			onClickButtonToButtonLayout();
			break;

		case R.id.buttonBlurPen:
			onClickButtonBlurPen();
			break;

		case R.id.buttonPlainPen:
			onClickButtonPlainPen();
			break;

		case R.id.exitButton:
			onClickButtonExit();
			break;

		case R.id.buttonEmboss:
			onClickButtonEmboss();
			break;

		case R.id.buttonDecom:
			onClickButtonShowPath();
			break;
		default:
			break;
		}
	}

	/**
	 * 点击浮动才当设置工具类型
	 */
	private void setToolTyle(int type) {
		mPaintView.setCurrentPainterType(type);
		mPenType = type;
		// toolsPopupWindow.dismiss();
	}

	/**
	 * 浮雕效果，在点击之后dissmiss
	 */
	private void onClickButtonEmboss() {
		setToolTyle(PEN_TYPE.EMBOSS);
	}

	/**
	 * 退出程序
	 */
	private void onClickButtonExit() {
		this.finish();
	}

	/**
	 * 点击铅笔功能
	 */
	private void onClickButtonPlainPen() {
		setToolTyle(PEN_TYPE.PLAIN_PEN);
	}

	/**
	 * blurPen
	 */
	private void onClickButtonBlurPen() {
		setToolTyle(PEN_TYPE.BLUR);
	}

	/**
	 * 当点击menu的时候将popupwindow伪装成menu显示
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// 点击menu
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			setAllLayoutInvisable();
		}
		// 点击返回
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			OkCancleDialog returnDialog = new OkCancleDialog(this,
					new OnClickOkListener() {
						@Override
						public void onClickOk() {
							finish();
						}
					});
			returnDialog.show();
			returnDialog.setMessage("确定要退出么？");
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


	/**
	 * 去ButtonLayout（主界面）
	 */
	private void onClickButtonToButtonLayout() {
		buttonLayout.setVisibility(View.VISIBLE);
		colorLayout.setVisibility(View.GONE);
	}

	/**
	 * redo
	 */
	private void onClickButtonRedo() {
		setAllLayoutInvisable();
		mPaintView.redo();
		upDateUndoRedo();
	}

	/**
	 * undo
	 */
	private void onClickButtonUndo() {
		setAllLayoutInvisable();
		mPaintView.undo();
		upDateUndoRedo();
	}

	/**
	 * 显示笔画分解
	 * 
	 * @return
	 */
	private boolean onClickButtonShowPath() {
		if (!mPaintView.canUndo())
			return false;
		else {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						while (mPaintView.canUndo()) {
							Message msg = mHandler.obtainMessage();
							msg.arg1 = 1;
							mHandler.sendMessage(msg);
							Thread.sleep(100);
						}
						Thread.sleep(500);
						while (mPaintView.canRedo()) {
							Message msg = mHandler.obtainMessage();
							msg.arg1 = 2;
							mHandler.sendMessage(msg);
							Thread.sleep(1700);
						}
						checkShowPath = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						checkShowPath = false;
					}
				}
			};
			new Thread(r).start();
			checkShowPath = true;
			return true;
		}
	}

	/**
	 * 更新UndoRedo Button
	 */
	private void upDateUndoRedo() {
		if (mPaintView.canUndo()) {
			enableUndoButton();
		} else {
			disableUndoButton();
		}
		if (mPaintView.canRedo()) {
			enableRedoButton();
		} else {
			disableRedoButton();
		}
	}

	private void enableRedoButton() {
		redoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.redo));
	}

	private void disableUndoButton() {
		undoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantundo));
	}

	/**
	 * color选择界面 如果是Eraser则改为前一个画笔
	 */
	private void onClickButtonColorSelect() {
		new ColorPickerDialog(this, new OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				setToLastPenTeype();
				mPaintView.setPenColor(color);
			}
		}, mPaintView.getPenColor()).show();
	}

	/**
	 * 改变画笔的大小
	 */
	private void onCLickButtonPen() {
		mPaintView.setCurrentPainterType(mPenType);
		if (penSizeLayout.isShown()) {
			setAllLayoutInvisable();
		} else {
			penSizeLayout.setVisibility(View.VISIBLE);
			shapLayout.setVisibility(View.VISIBLE);
			shapLayoutf.setVisibility(View.VISIBLE);
			eraserSizeLayout.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 将所有的布局全都隐藏
	 */
	private void setAllLayoutInvisable() {
		shapLayout.setVisibility(View.INVISIBLE);
		shapLayoutf.setVisibility(View.INVISIBLE);
		penSizeLayout.setVisibility(View.INVISIBLE);
		eraserSizeLayout.setVisibility(View.INVISIBLE);
	}

	/**
	 * 橡皮
	 */
	private void onClickButtonEraser() {
		if (eraserSizeLayout.isShown()) {
			setAllLayoutInvisable();
		} else {
			eraserSizeLayout.setVisibility(View.VISIBLE);
			penSizeLayout.setVisibility(View.INVISIBLE);
			shapLayout.setVisibility(View.INVISIBLE);
			shapLayoutf.setVisibility(View.INVISIBLE);
		}
		mPaintView.setCurrentPainterType(PEN_TYPE.ERASER);
	}

	/**
	 * 清空
	 */
	private void onClickButtonClear() {
		setAllLayoutInvisable();
		clearDialog();
	}

	/**
	 * 清空对话框
	 */
	private void clearDialog() {
		OkCancleDialog dialog = new OkCancleDialog(this,
				new OnClickOkListener() {
					@Override
					public void onClickOk() {
						mPaintView.clearAll();
						mPaintView.resetState();
						upDateUndoRedo();
						upDateColorView();
						resetSizeView();
					}
				});
		dialog.show();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setMessage("确定要清空当前图画么？\n您未保存的修改将丢失");
	}

	/**
	 * 将ColorView的Check清空
	 */
	private void upDateColorView() {
		colorRadioGroup.clearCheck();
	}

	/**
	 * 重置sizeView
	 */
	private void resetSizeView() {
		penSizeRadio1.setChecked(true);
		eraserSizeRadio01.setChecked(true);
		curvRadioButton.setChecked(true);
	}

	/**
	 * 载入图片
	 */
	private void onClickButtonLoad() {
		setAllLayoutInvisable();
		// 点击Load时要对数据库进行更新
		sendUpdateBroadCast();
		startLoadActivity();
	}

	/**
	 * 发送广播，更新sd卡中的数据库
	 */
	private void sendUpdateBroadCast() {
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addDataScheme("file");
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://"
						+ Environment.getExternalStorageDirectory()
								.getAbsolutePath())));
	}

	/**
	 * 保存
	 */
	private void onClickButtonSave() {
		setAllLayoutInvisable();
		boolean sdCardIsMounted = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardIsMounted) {
			OkDialog okDialog = new OkDialog(this, new OnClickOkListener() {
				@Override
				public void onClickOk() {
				}
			});
			okDialog.show();
			okDialog.setMessage("请插入存储卡");
		} else {
			SaveDialog dialog = new SaveDialog(this,
					new EditTextDialogListener() {
						// 当点击确定的时候自动调用 getDialogText接口
						@Override
						public void getDialogText(String string) {
							String sdDir = getDirPath();
							String file = sdDir + string + ".png";
							Bitmap bitmap = mPaintView.getSnapShoot();
							BitMapUtils.saveToSdCard(file, bitmap);
							sendUpdateBroadCast();
						}
					});
			dialog.show();
		}
	}

	/**
	 * 开始载入activiy, 发送imageIntent，寻找合适的程序载入
	 */
	private void startLoadActivity() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PaintConstants.LOAD_ACTIVITY);
	}

	/**
	 * 回调函数，处理读取和语音消息
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PaintConstants.LOAD_ACTIVITY:
			if (data != null) {
				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();
				try {
					Bitmap bitmap;
					BitmapFactory.Options op = new BitmapFactory.Options();
					op.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(cr.openInputStream(uri), null,
							op);
					int wRatio = (int) Math.ceil(op.outWidth
							/ (float) mPaintView.getWidth());
					int hRatio = (int) Math.ceil(op.outHeight
							/ (float) mPaintView.getHeight());
					// 如果超出指定大小，则缩小相应的比例
					if (wRatio > 1 && hRatio > 1) {
						if (wRatio > hRatio) {
							op.inSampleSize = wRatio;
						} else {
							op.inSampleSize = hRatio;
						}
					}
					op.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(
							cr.openInputStream(uri), null, op);
					bitmap = BitmapFactory
							.decodeStream(cr.openInputStream(uri));
					mPaintView.setForeBitMap(bitmap);
					mPaintView.resetState();
					upDateUndoRedo();
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
						bitmap = null;
					}
				} catch (Exception e) {
					return;
				}
			}
			break;

		case VOICE_RECOGNITION_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				for (int i = 0; i < matches.size(); i++) {
					if (matches.get(i).equals("小狗")) {
						getResource(R.drawable.dog);
						System.out.println("true");
						break;
					} else {
						System.out.println("false");
					}
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 绘制图片
	 */
	private void getResource(int resource) {
		Bitmap bitmap;
		// int resource = R.drawable.dog;
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(this.getResources(), resource, op);
		int wRatio = (int) Math.ceil(op.outWidth
				/ (float) mPaintView.getWidth());
		int hRatio = (int) Math.ceil(op.outHeight
				/ (float) mPaintView.getHeight());
		// 如果超出指定大小，则缩小相应的比例
		if (wRatio > 1 && hRatio > 1) {
			if (wRatio > hRatio) {
				op.inSampleSize = wRatio;
			} else {
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bitmap = BitmapFactory
				.decodeResource(this.getResources(), resource, op);
		bitmap = BitmapFactory.decodeResource(this.getResources(), resource);
		mPaintView.setForeBitMap(bitmap);
		mPaintView.resetState();
		upDateUndoRedo();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 得到存储路径
	 */
	private String getDirPath() {
		File sdcarddir = android.os.Environment.getExternalStorageDirectory();
		String dirString = sdcarddir.getPath() + "/paintPad/";
		File filePath = new File(dirString);
		if (!filePath.exists()) {
			// 如果无法创建
			if (!filePath.mkdirs()) {
				OkDialog dialog = new OkDialog(this, new OnClickOkListener() {
					@Override
					public void onClickOk() {

					}
				});
				dialog.show();
				dialog.setMessage("无法在sd卡中创建目录/paintPad, \n请检查SDCard");
			}
		}
		return dirString;
	}

	private void enableUndoButton() {
		undoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.undo));
	}

	private void disableRedoButton() {
		redoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantredo));
	}

	/**
	 * 语音输入方法
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出绘制需要绘制的图片:");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

}
