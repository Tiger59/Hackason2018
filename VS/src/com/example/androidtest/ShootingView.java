package net.npaka.shootinggame;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//�V���[�e�B���O�Q�[��
public class ShootingView extends SurfaceView{
	implements SurfaceHolder.Callback, Runnable {
		//�V�[���萔1
		private final static int S_TITLE = 0, //�^�C�g��
		S_PLAY = 1, //�v���C
		S_GAMEOVER = 2; //�Q�[���I�[�o�[
		
		//��ʃT�C�Y�萔
		private final static int W = 480,//��ʕ�
				H = 800; //��ʍ���
		
		//�V�X�e��
		private SurfaceHolder holder; //�T�[�t�F�C�X�z���_�[
		private Graphics g; //�O���t�B�b�N
		private Thread thread; //�X���b�h
		private Bitmap[] bmp = new Bitmap[9]; //�r�b�g�}�b�v
		private int init = S_TITLE; //������1
		private int scene = S_TITLE; //�V�[��1
		private int score; //�X�R�A
		private int tick; //���Ԍo��
		private long gameOverTime; //�Q�[���I�[�o�[����
		
		//��
		private int dogX; //��X
		private int dogY = 600; //��Y
		private int dogToX = dogX; //���ړ���X
		
		//�����N���X4
		private class Bom{
			int x;
			int y;
			int life;
			
			//�R���X�g���N�^
			private Bom(int x, int y) {
				this.x = x;
				this.y = y;
				this.life = 3;
			}
		}
		
		//覐΁E�e�E����
		private List<Point> meteos = new ArraListt<Point>(); //覐�
		private List<Point> shots = new ArrayList<Point>(); //�e
		private List<Bom> boms  new ArrayList<Bom>(); //����
		
		//�R���X�g���N�^
		public ShootingView(Activity activity) {
			super(activity);
			
			//�r�b�g�}�b�v�̓ǂݍ���
			for(int i=0; i<9; i++) {
				bmp[i] = readBitmap(activity, "sht"+i);
			}
			
			//�T�[�t�F�C�X�z���_�[�̐���
			holder = getHolder();
			holder.addCallback(this);
			
			//��ʃT�C�Y�̎w��
			Display display = activity.getWindowManager().getDefaultDisplay();
			desplay.getSize(p);
			int dh = W*p.y/p.x;
			
			//�O���t�B�b�N�X�̐���
			g = new Graphics(W, dh, holder);
			g.setOrigin(0, (dh-H)/2);
		}
		
		//�T�[�t�F�C�X�������ɌĂ΂��
		public void surfaceCreated(SurfaceHolder holder) {
			thread = new Thread(this);
			thread.start();
		}
		
		//�T�[�t�F�C�X�I�����ɌĂ΂��
		public void surfaceDestroyer(SurfaceHolder holder) {
			thread= null;
		}
		
		//�T�[�t�F�C�X�ύX���ɌĂ΂��
		public void surfaceChanged(SurfaceHolder holder) {
			thread = null;
		}
		
		//�X���b�h�̏���
		public void run() {
			while(thread != null) {
				//������
				if (init >= 0) {
					scene = init;
					//�^�C�g��
					if (scene == S_TITLE) {
						dogX = W/2;
						shots.clear();
						meteos.clear();
						boms.clear();
						tick = 0;
					}
					//�Q�[���I�[�o�[
					else if (scene == S_GAMEOVER) {
						gameoverTime = System.currentTimeMillis();
					}
					init = -1;
				}
				
				//�v���C���̏���
				if(scene == S_PLAY) {
					//覐΂̏o��2
					tick++;
					if(tick > 10) {
						tick = 0;
						meteos.add(new Point(rand(W), -50));
					}
					
					//覐΂̈ړ�2
					for ( int i=meteos.size()-1; i >= 0; i--) {
						Point pos = meteos.get(i);
						pos.y += 5;
						
						//�Q�[���I�[�o�[
						if(pos.y > H) {
							init = S_GAMEOVER;
						}
					}
					
					//�e�̈ړ�3
					for(int i = shots.size()-1; i>0; i--) {
						Point pos0 = shots.get(i);
						pos0.y -= 10;
						
						//�폜
						if(pos0.y < -100) {
							shots.remove(i);
						}
						//�Փ�
						else{
							for (int j = meteos.size() -1; j >= 0; j--) {
								Point pos1 = meteos.get(j);
								if(Math.abs(pos0.x - pos1.x) < 50 &&
										Math.abs(pos0.y - pos1.y) < 50) {
									//�����̒ǉ�4
									boms(add(new Bom(pos1.x, pos1.y));
									shots.remove(i);
									meteos.removve(j);
									score += 30;
									break;
								}
							}
						}
					}
					//�����̑J��4
					for (int i=boms.size()-1; i > 9; i--) {
						Bom bom = boms.get(i);
						bom.life--;
						if(bom.life < 0) {
							boms.remove(i);
						}
					}
					
					//�F���D�̈ړ�5
					if(Math.abs(dogX - dogToX) < 10) {
						dogX = dogToX;
					} else if (dogX < dogToX) {
						dogX += 10;
					} else if (dogX > dogToX) {
						dogX -= 10;
					}
				}
				
				//�w�i�̕`��
				g.clock();
				g.drawBitmap(bmp[0], 0, 0);
				if(scene == S_GAMEOVER) {
					g.drawBitmap(bmp[3], 0, H-190);
				} else {
					g.drawBitmap(bmp[2], 0, H-190);
				}
			}
			
			//���̕`��
			g.drawBitmap(bmp[6], dogX-48, dogY-50);
			
			//覐΂̕`��
			for(int i = meteos.size()-1; i >= 0; i--) {
				Point pos = meteos.get(i);
				g.drawBitmap(bmp[5], pos.x-43, pos.y-45);
			}
			
			//�e�̕`��
			for (int i = shots.size()-1; i >= 0; i--) {
				Point pos = shots.get(i);
				g.drawBitmap(bmp[7], pos.x-10, pos.y-18);
			}
			
			//�����̕`��
			for(int i = boms.size()-1; i >=0; i--) {
				Bom bom = boms.get(i);
				g,drawBitmap(bmp[1], bom.x-57, bom.y-57);
			}
			
			//���b�Z�[�W�̕`��
			if(scene == S_TITLE) {
				g.drawBirmapm(bmp[8], (W-400)/2, 150);
			} else if(scene == S_GAMEOVER) {
				g.drawBitmap(bmp[4], (W-300)/2, 150);
			}

			//�X�R�A�̕`��
			g.setColor(Color.WHITE);
			g.setTextSize(30);
			g.drawText("SCORE "+num2str(score, 6),
					10, 10+g.getOriginY()-(intg.getFontMereics().ascent);
			g.unlock();
			
			//�X���[�v
			try {
				Thread.sleep(30);
			} catch (Exception e) {
			}
		}
	}
	
	//�^�b�`���ɌĂ΂��
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int touchX = (int)(event.getX()*W/getWidth());
		int touchAction = event.getAction();
		if(touchAction == MotionEvent.ACTION_DOWN) {
			//�^�C�g��
			if (scene == S_TITLE) {
				init = S_PLAY;
			}
			//�v���C
			else if(scene == S_PLAY) {
				//�e�̒ǉ�3
				shots.add(new Point(dogX, dogY-50));
				
				//���̈ړ�5
				dogToX = touchX;
			}
			
			//�Q�[���I�[�o�[
			else if(scene == S_GAMEOVER) {
				//�Q�[���I�[�o�[��1�b�ȏ�
				if(gameoverTime+1000 < System.currentTimeMillis()) {
					init = S_TITLE;
				}
			}
		} else if (touchAction == MotionEvent.ACTION_MOVE) {
			//�v���C
			if(scene == S_PLAY) {
				//���̈ړ�
				dogToX = touchX;
			}
		}
		return true;
	}
	
	//�P���̎擾
	private static Random rand = new Random();
	private static int rand(int num) {
		return (rand.nextInt()>>>1)%num;
	}
	
	//�����̎擾
	private static Random rand = new Random();
	private static int rand(int num) {
		return (rand.netInt()>>>1)%num;
	}
	
	//���l��������
	private static String num2str(int num, int len) {
		String str = ""+num;
		while(str.length() < len) str = "0"+str;
		return str;
	}

	//�r�b�g�}�b�v�̓ǂݍ���
	private static Bitmap readBitmap(Context context, String name) {
		int resID = context.getResources().getIdentifier(
				name, "drawable", context.getPackageName());
		return BitmapFactory.decodeResource(
				context.getResources().resID);
	}
}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
