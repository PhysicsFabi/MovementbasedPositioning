package edu.uv.students.mobiledevices.sensorbasedpositioning.visualization;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.*;
import java.util.HashSet;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class ProcessingVisualization extends PApplet {



    FiniteStateMachine sm;
    VisualizationState visualizationState;
    AndroidProcessingInterface androidInterface;

    public final float MIN_SHOWN_METERS_ZOOM_IN=5.f;
    public final float MIN_SHOW_METERS_ZOOM_INITIAL=10.f;
    public final float MIN_SHOWN_METERS_ZOOM_OUT=30.f;

    private List<PVector> path;
    private float direction;

    public void setAndroidInterface(AndroidProcessingInterface pAndroidInterface) {
        androidInterface=pAndroidInterface;
        androidInterface.setProcessingVisualization(this);
    }

    public void onPathChanged() {
        if(androidInterface==null)
            return;
        path=androidInterface.getPath();
        direction=androidInterface.getDirection();
    }

    public void onLowSensorAccuracy() {
        visualizationState.onLowSensorAccuracy();
    }

    public void settings() {
        fullScreen();
    }

    public void setup() {
        reset();
        sm=new FiniteStateMachine();
        sm.addState(new DescriptionState());
        visualizationState = new VisualizationState();
        sm.addState(visualizationState);
    }


    public void draw() {
        sm.draw();
    }

    public void reset() {
        if(visualizationState!=null)
            visualizationState.reset();
    }
    public interface AndroidProcessingInterface {

        public void onReset();
        public void startDeadReckoning();
        public void saveScreenshot(PImage pScreenshot);
        public List<PVector> getPath();
        public void setProcessingVisualization(ProcessingVisualization pProcessingVisualization);
        public float getDirection();
    }
    class BackgroundGrid implements Drawable {
        final int w;
        final int h;
        int n_w;
        int n_h;
        int blockW;
        int offsetX_p;
        int offsetY_p;
        int offsetX_origin_p;
        int offsetY_origin_p;

        BackgroundGrid(int pW, int pH) {
            w=pW;
            h=pH;
        }

        public void draw() {
            strokeWeight(0);
            pushMatrix();
            translate(offsetX_p, offsetY_p);
            for (int i = 0; i <= n_w; i ++) {
                for (int j = 0; j <= n_h; j ++) {
                    if ((i + j + 1) % 2 == 0) {
                        fill(255, 255, 255); // white
                    } else {
                        fill(0xff1da1f2); // black
                    }
                    rect(i * blockW, j * blockW, blockW, blockW, blockW/7.5f);
                }
            }
            fill(0xff000000);
            textSize(0.5f*blockW);
            textAlign(CENTER, CENTER);
            text("1m",2*blockW,(n_h-2)*blockW,blockW,blockW);
            popMatrix();
        }

        public void calculateParameters() {
            blockW=metersToPixels(1);
            n_w=ceil((float)w/(float)blockW)+1;
            n_h=ceil((float)h/(float)blockW)+1;
            int zeroGridPointX_p=ceil(n_w/2.0f)*blockW;
            int zeroGridPointY_p=ceil(n_h/2.0f)*blockW;
            offsetX_p = floor(w/2.0f-zeroGridPointX_p);
            offsetY_p = floor(h/2.0f-zeroGridPointY_p);
            offsetX_origin_p = offsetX_p+zeroGridPointX_p;
            offsetY_origin_p = offsetY_p+zeroGridPointY_p;
        }
    }
    class DescriptionState extends State {

        TransitRectButton startButton;
        int buttonH;
        int buttonW;
        int buttonPadding;

        PFont headingFont;
        int headingTextSize;
        int headingW;
        int headingH;
        int headingPaddingTop;
        int headingPaddingLeft;
        String headingText = "Dead-Reckoning";


        public DescriptionState() {
            super("DESCRIPTION_STATE");
            buttonW=floor(0.95f*width);
            buttonH=floor(0.07f*height);
            buttonPadding=floor(0.5f*(width-buttonW));

            headingW=floor(0.9f*width);
            headingH=floor(0.2f*height);
            headingPaddingTop=floor(0.1f*height);
            headingPaddingLeft=floor((width-headingW)/2.0f);
            headingTextSize = findFontSizeToFitBox(headingW,headingH, headingText);
            headingFont = createDefaultFont(headingTextSize);

            startButton = new TransitRectButton(
                    "VISUALIZATION_STATE",
                    this,
                    new FadingTransitAnimation(),
                    new TextRectButtonRenderer("Start",buttonW,buttonH),
                    buttonPadding,height-buttonPadding-buttonH,buttonW,buttonH);
            drawables.add(startButton);
        }

        public void draw() {
            super.draw();
            pushStyle();
            fill(0xff000000);
            textSize(headingTextSize);
            textFont(headingFont);
            textAlign(CENTER,CENTER);
            text(headingText,headingPaddingLeft,headingPaddingTop,headingW,headingH);
            popStyle();


        }
    }
    class Figurine implements Drawable {
        PImage userImg;

        Figurine() {
            userImg = resize(loadImage("user.png"), floor(0.06f*width), 0);
        }

        public void draw() {
            imageMode(CENTER);
            image(userImg,0,0);
            imageMode(CORNER);
        }
    }
    class State implements Drawable {
        final String ID;
        protected ArrayList<Drawable> drawables;
        private FiniteStateMachine stateMachine;

        State(String pId) {
            ID=pId;
            drawables=new ArrayList<Drawable>();
        }

        public void draw() {
            for(Drawable d : drawables)
                d.draw();
        }

        public void addDrawable(Drawable pDrawable) {
            drawables.add(pDrawable);
        }

        public void prepareToAppear(State fromState) {}

        public void prepareToDisappear(State toState) {}

        public void OnDisappear() {}

        public void OnAppear() {}

        public void setStateMachine(FiniteStateMachine pStateMachine) {
            stateMachine=pStateMachine;
        }

        public FiniteStateMachine getStateMachine() {
            return stateMachine;
        }

    }

    class StateWithBGImage extends State {
        PImage bgImage;

        StateWithBGImage(String pId,String pFileName) {
            this(pId,loadImage(pFileName));
        }

        StateWithBGImage(String pId,PImage pBGImage) {
            super(pId);
            bgImage=resizeToCoverBox(pBGImage,width,height);
        }

        public void draw() {
            drawBGImage();
            super.draw();
        }

        public void drawBGImage() {
            image(bgImage,0,0);
        }
    }

    interface TransitAnimation {
        public boolean drawAnimatedTransit(State fromState, State toState);
        public void reset();
    }

    class FadingTransitAnimation implements TransitAnimation {
        int durationMilliSec;
        int fadingColor;

        int animationStartInMilliSec;
        float alphaPerMilliSec;
        boolean fadeOut;

        FadingTransitAnimation() {
            this(2200);
        }

        FadingTransitAnimation(int pDurationMilliSec) {
            this(pDurationMilliSec,0xffFFFFFF);
        }

        FadingTransitAnimation(int pDurationMilliSec, int pFadingColor) {
            durationMilliSec=pDurationMilliSec;
            alphaPerMilliSec=255.0f/(durationMilliSec/2.0f);
            fadingColor=pFadingColor;
            reset();
        }

        public void reset() {
            fadeOut=true;
            animationStartInMilliSec=-1;
        }

        public boolean drawAnimatedTransit(State fromState, State toState) {
            if(animationStartInMilliSec<0)
                animationStartInMilliSec=millis();
            strokeWeight(0.0f);
            stroke(0xffFFFFFF);
            int passedTimeInMilliSec=millis()-animationStartInMilliSec;

            if(fadeOut) {
                fromState.draw();
                int alpha=floor(passedTimeInMilliSec*alphaPerMilliSec);
                fill(fadingColor,min(alpha,255));
                rect(0,0,width,height);
                if(alpha>=255) {
                    fadeOut=false;
                }
            } else {
                passedTimeInMilliSec-=0.5f*durationMilliSec;
                int alpha=floor(255.0f-passedTimeInMilliSec*alphaPerMilliSec);
                toState.draw();
                fill(fadingColor,max(alpha,0));
                rect(0,0,width,height);
                if(alpha<=0) {
                    return false;
                }
            }
            return true;
        }
    }

    class FiniteStateMachine implements Drawable {
        HashMap<String,State> states;
        State currentState;
        State nextState;
        TransitAnimation currentTransitAnimation;


        FiniteStateMachine() {
            states=new HashMap<String,State>();
            currentState=null;
            nextState=null;
        }

        public void addState(State pState) {
            if(currentState==null)
                currentState=pState;
            states.put(pState.ID, pState);
            pState.setStateMachine(this);
        }

        public State getState(String pStateId) {
            return states.get(pStateId);
        }

        public void draw() {
            if(currentTransitAnimation!=null) {
                drawTransit();
                return;
            }
            if(currentState!=null)
                currentState.draw();
        }

        public void drawTransit() {
            if(!currentTransitAnimation.drawAnimatedTransit(currentState,nextState))
                finalizeTransit();
        }

        private void finalizeTransit() {
            currentState.OnDisappear();
            nextState.OnAppear();
            currentState=nextState;
            nextState=null;
            currentTransitAnimation=null;
        }

        public void transit(String pToState,TransitAnimation pTransitAnimation) {
            transit(states.get(pToState),pTransitAnimation);
        }

        public void transit(String pToState) {
            transit(states.get(pToState),null);
        }

        public void transit(State pToState,TransitAnimation pTransitAnimation) {
            if(nextState!=null)
                return;
            currentTransitAnimation=pTransitAnimation;
            if(currentTransitAnimation!=null)
                currentTransitAnimation.reset();
            nextState=pToState;
            currentState.prepareToDisappear(nextState);
            nextState.prepareToAppear(currentState);
            if(pTransitAnimation==null)
                finalizeTransit();
        }
    }

    class TransitRectButton extends RectButton implements OnRectButtonEventListener {
        String transitToStateId;
        State transitFromState;
        TransitAnimation transitAnimation;

        TransitRectButton(
                String pTransitToStateId,
                State pTransitFromState,
                TransitAnimation pTransitAnimation,
                RectButtonRenderer pRectButtonRenderer,
                float pX,float pY,float pWidth,float pHeight) {
            super(pX,pY,pWidth,pHeight,null,pRectButtonRenderer);
            transitToStateId=pTransitToStateId;
            transitFromState=pTransitFromState;
            transitAnimation=pTransitAnimation;
            eventListener=this;
        }

        TransitRectButton(
                String pTransitToStateId,
                State pTransitFromState,
                RectButtonRenderer pRectButtonRenderer,
                float pX,float pY,float pWidth,float pHeight) {
            this(pTransitToStateId,pTransitFromState,null,pRectButtonRenderer,pX,pY,pWidth,pHeight);
        }

        public void onMouseClicked(RectButton pButton) {
            transitFromState.getStateMachine().transit(transitToStateId,transitAnimation);
        }

        public void onMouseOver(RectButton pButton){}
        public void onMouseOut(RectButton pButton){}
        public void onMousePressed(RectButton pButton){}
        public void onMouseReleased(RectButton pButton){}
    }
    interface OnZoomChangedListener {
        public void onZoomChanged(float minShownZoom);
    }

    class Hud implements Drawable, OnPercentageSelectorChangedListener {

        int w;
        int h;
        int compassRoseW;
        int compassRosePadding;

        PImage compassRoseImg;

        RectButton saveButton;
        RectButton resetButton;
        int buttonH;
        int buttonW;
        int buttonPadding;

        PercentageSelector zoomSelector;
        int zoomSelectorW;
        int zoomSelectorH;
        int zoomSelectorPadding;
        float initalPercentage;

        private OnZoomChangedListener onZoomChangedListener;

        public Hud(int pW, int pH, OnZoomChangedListener pOnZoomChangedListener) {
            w=pW;
            h=pH;
            onZoomChangedListener=pOnZoomChangedListener;
            compassRoseW = floor(0.1f*w);
            compassRosePadding = floor(0.01f*w);
            compassRoseImg = resize(loadImage("compass_rose.png"), compassRoseW, 0);

            buttonH=floor(0.07f*h);
            buttonW=3*buttonH;
            buttonPadding=floor(0.05f*buttonW);
            saveButton = new RectButton("Save", w-2*buttonW-2*buttonPadding, h-buttonPadding-buttonH, buttonW, buttonH, new OnRectButtonEventListener(){
                public void onMouseClicked(RectButton pButton){
                    if(androidInterface!=null) {
                        PImage screenshot = get(0,0,width,height);
                        androidInterface.saveScreenshot(screenshot);
                    }
                }
                public void onMouseOver(RectButton pButton){}
                public void onMouseOut(RectButton pButton){}
                public void onMousePressed(RectButton pButton){}
                public void onMouseReleased(RectButton pButton){}
            });
            resetButton = new RectButton("Reset", w-buttonW-buttonPadding, h-buttonPadding-buttonH, buttonW, buttonH, new OnRectButtonEventListener(){
                public void onMouseClicked(RectButton pButton){
                    if(androidInterface!=null)
                        androidInterface.onReset();
                    reset();
                }
                public void onMouseOver(RectButton pButton){}
                public void onMouseOut(RectButton pButton){}
                public void onMousePressed(RectButton pButton){}
                public void onMouseReleased(RectButton pButton){}
            });

            zoomSelectorW=floor(0.7f*width);
            zoomSelectorH=buttonH;
            zoomSelectorPadding=buttonPadding;
            initalPercentage = 1.f-(MIN_SHOW_METERS_ZOOM_INITIAL-MIN_SHOWN_METERS_ZOOM_IN)/(MIN_SHOWN_METERS_ZOOM_OUT-MIN_SHOWN_METERS_ZOOM_IN);
            zoomSelector = new PercentageSelector(width-zoomSelectorPadding-zoomSelectorW,buttonPadding,zoomSelectorW,zoomSelectorH,initalPercentage,8,this);

        }

        public void resetToInitialZoom() {
            zoomSelector.setPercentage(initalPercentage);
        }

        public void draw() {
            image(compassRoseImg, compassRosePadding, compassRosePadding);
            saveButton.draw();
            resetButton.draw();
            zoomSelector.draw();
        }

        public void onPercentageChanged(float pPercentage, PercentageSelector pSelector) {
            float minShownMeters = (1.f-pPercentage)*(MIN_SHOWN_METERS_ZOOM_OUT-MIN_SHOWN_METERS_ZOOM_IN)+MIN_SHOWN_METERS_ZOOM_IN;
            if(onZoomChangedListener!=null)
                onZoomChangedListener.onZoomChanged(minShownMeters);
        }
    }
    interface OnPercentageSelectorChangedListener {
        public void onPercentageChanged(float pPercentage, PercentageSelector pSelector);
    }


    interface PercentageSelectorRenderer {
        public void render(int pX,int pY,int pWidth,int pHeight,float percentage);
    }

    class DefaultPercentageSelectorRenderer implements PercentageSelectorRenderer {
        float buttonPaddingRel=0.03f;

        public void render(int pX,int pY,int pWidth,int pHeight,float percentage) {
            strokeWeight(0);
            stroke(0x00FFFFFF);
            fill(0xffFFFFFF);
            rect(pX+buttonPaddingRel*pWidth,pY,(1.0f-2.0f*buttonPaddingRel)*pWidth,pHeight);

            strokeWeight(0.05f*pHeight);
            stroke(0xff000000);
            fill(0x00FFFFFF);
            rect(pX+buttonPaddingRel*pWidth,pY,(1.0f-2.0f*buttonPaddingRel)*pWidth,pHeight);

            strokeWeight(0);
            stroke(0x00FFFFFF);
            fill(0xff000000);
            rect(pX+buttonPaddingRel*pWidth,pY,(1.0f-2.0f*buttonPaddingRel)*pWidth*percentage,pHeight);
        }
    }


    class PercentageSelector implements Drawable {
        int x;
        int y;
        int w;
        int h;
        private RectButton minusButton;
        private RectButton plusButton;
        private PercentageSelectorRenderer renderer;
        private OnPercentageSelectorChangedListener listener;
        private float percentage;

        private int steps;
        private int selectionAreaX;
        private int selectionAreaY;
        private int selectionAreaWidth;
        private int selectionAreaHeight;
        private float percentageStep;

        PercentageSelector(int pX,int pY,int pWidth,int pHeight,float pInitalPercentage,int pSteps,PercentageSelectorRenderer pRenderer, OnPercentageSelectorChangedListener pListener) {
            x=pX;
            y=pY;
            w=pWidth;
            h=pHeight;
            percentage=pInitalPercentage;
            steps=pSteps;
            percentageStep=1.0f/steps;
            renderer=pRenderer;
            listener=pListener;
            measurements();
            setupButtons();
            notifiyListener();
        }

        PercentageSelector(int pX,int pY,int pWidth,int pHeight,float pInitalPercentage,int pSteps, OnPercentageSelectorChangedListener pListener) {
            this(pX,pY,pWidth,pHeight,pInitalPercentage,pSteps,new DefaultPercentageSelectorRenderer(), pListener);
        }

        public void setupButtons() {
            minusButton=new RectButton("-",x,y,h,h,new OnRectButtonEventListener() {
                public void onMouseClicked(RectButton pButton) {
                    percentage=max(0.0f,percentage-percentageStep);
                    notifiyListener();
                }
                public void onMouseOver(RectButton pButton){}
                public void onMouseOut(RectButton pButton){}
                public void onMousePressed(RectButton pButton){}
                public void onMouseReleased(RectButton pButton){}
            });
            plusButton=new RectButton("+",x+w-h,y,h,h,new OnRectButtonEventListener() {
                public void onMouseClicked(RectButton pButton) {
                    percentage=min(1.0f,percentage+percentageStep);
                    notifiyListener();
                }
                public void onMouseOver(RectButton pButton){}
                public void onMouseOut(RectButton pButton){}
                public void onMousePressed(RectButton pButton){}
                public void onMouseReleased(RectButton pButton){}
            });
        }


        private void measurements() {
            selectionAreaX=x+h;
            selectionAreaY=y;
            selectionAreaWidth=w-2*h;
            selectionAreaHeight=h;
        }

        public void setPercentage(float pPercentage) {
            percentage=pPercentage;
            notifiyListener();
        }

        public void draw() {
            minusButton.draw();
            plusButton.draw();
            renderer.render(selectionAreaX,selectionAreaY,selectionAreaWidth,selectionAreaHeight,percentage);
        }

        public void notifiyListener() {
            if(listener!=null)
                listener.onPercentageChanged(percentage, this);
        }
    }


    static interface MouseProspect {
        public boolean isInside(float pX,float pY);
        // methods are called every frame
        public void notifyHovered();
        public void notifyNotHovered();
        public void notifyPressed();
        public void notifyReleased();
    }

    static interface OnRectButtonEventListener {
        // methods are called once (and not every frame)
        // onMouseClick is not fired for disabled buttons
        public void onMouseClicked(RectButton pButton);
        public void onMouseOver(RectButton pButton);
        public void onMouseOut(RectButton pButton);
        public void onMousePressed(RectButton pButton);
        public void onMouseReleased(RectButton pButton);
    }

    static class ButtonRenderState {
        static final int BT_NORMAL=0;
        static final int BT_HOVERED=1;
        static final int BT_PRESSED=2;
        static final int BT_DISABLED=3;
    }

    static interface RectButtonRenderer {
        public void render(float pButtonX,float pButtonY,float pButtonWidth,float pButtonHeight,int pButtonState);
    }

    public void handleMouseProspect(MouseProspect pMouseProspects) {
        if(pMouseProspects.isInside(mouseX,mouseY)) {
            pMouseProspects.notifyHovered();
            if(mousePressed)
                pMouseProspects.notifyPressed();
            else
                pMouseProspects.notifyReleased();
        } else {
            pMouseProspects.notifyNotHovered();
        }
    }

    class RectButtonRendererBase implements RectButtonRenderer {
        int[] bgColors;
        float[] strokeWeightsRelativeToW;
        int[] strokeColors;

        RectButtonRendererBase(int[] pBgColors,float[] pStrokeWeights,int[] pStrokeColors) {
            bgColors=pBgColors.clone();
            strokeWeightsRelativeToW=pStrokeWeights.clone();
            strokeColors=pStrokeColors.clone();
        }

        RectButtonRendererBase() {
            bgColors=new int[]{0xffFFFFFF,0xffFFFFFF,0xff0A81FC,0xffA5A5A5};
            strokeWeightsRelativeToW=new float[]{.002f,0.002f,.004f,.002f};
            strokeColors=new int[]{0xff000000,0xff000000,0xff000000,0xff959595};;
        }

        public void render(float pButtonX,float pButtonY,float pButtonWidth,float pButtonHeight,int pButtonRenderState) {
            pushStyle();
            fill(bgColors[pButtonRenderState]);
            strokeWeight(strokeWeightsRelativeToW[pButtonRenderState]*pButtonWidth);
            stroke(strokeColors[pButtonRenderState]);
            rect(pButtonX,pButtonY,pButtonWidth,pButtonHeight);
            popStyle();
        }
    }

    class TextRectButtonRenderer extends RectButtonRendererBase {
        String[] texts;
        int[] textColors;
        PFont[] fonts;
        int[] fontSizes;
        float[] textPaddingsLeftRelativeToW;
        float[] textPaddingsTopRelativeToH;
        float[] textPaddingsRightRelativeToW;
        float[] textPaddingsBottomRelativeToH;
        int[] textHorizontalAlignments;
        int[] textVerticalAlignments;
        float[] textStrokeWeights;
        int[] textStrokeColors;
        float w;
        float h;

        static final int defaultFontSize=14;
        PFont defaultFont;

        public PFont defaultFont() {
            if(defaultFont==null)
                defaultFont=createDefaultFont(defaultFontSize);
            return defaultFont;
        }

        TextRectButtonRenderer(
                String[] pTexts,
                int[] pTextColors,
                PFont[] pFonts,
                int[] pFontSizes,
                float[] pTextPaddingsLeftRelativeToW,
                float[] pTextPaddingsTopRelativeToH,
                float[] pTextPaddingsRightRelativeToW,
                float[] pTextPaddingsBottomRelativeToH,
                int[] pTextHorizontalAlignments,
                int[] pTextVerticalAlignments,
                float[] pTextStrokeWeights,
                int[] pTextStrokeColors,
                float pButtonWidth,
                float pButtonHeight
        ) {
            texts=pTexts.clone();
            textColors=pTextColors.clone();
            fonts=pFonts==null?null:pFonts.clone();
            fontSizes=pFontSizes==null?null:pFontSizes.clone();
            textPaddingsLeftRelativeToW=pTextPaddingsLeftRelativeToW.clone();
            textPaddingsTopRelativeToH=pTextPaddingsTopRelativeToH.clone();
            textPaddingsRightRelativeToW=pTextPaddingsRightRelativeToW.clone();
            textPaddingsBottomRelativeToH=pTextPaddingsBottomRelativeToH.clone();
            textHorizontalAlignments=pTextHorizontalAlignments;
            textVerticalAlignments=pTextVerticalAlignments;
            textStrokeWeights=pTextStrokeWeights;
            textStrokeColors=pTextStrokeColors;
            w=pButtonWidth;
            h=pButtonHeight;
            if(fonts==null)
                fonts=new PFont[]{createDefaultFont(defaultFontSize),createDefaultFont(defaultFontSize),createDefaultFont(defaultFontSize),createDefaultFont(defaultFontSize)};
            if(fontSizes==null) {
                fontSizes=new int[]{
                        fonts[ButtonRenderState.BT_NORMAL].getSize(),
                        fonts[ButtonRenderState.BT_HOVERED].getSize(),
                        fonts[ButtonRenderState.BT_PRESSED].getSize(),
                        fonts[ButtonRenderState.BT_DISABLED].getSize()};
            }
        }

        TextRectButtonRenderer(String pText,float pButtonWidth,float pButtonHeight) {
            this(
                    new String[]{pText,pText,pText,pText},
                    new int[]{0xff000000,0xff000000,0xff000000,0xffDDDDDD},
                    null,
                    null,
                    new float[]{0.05f,0.05f,0.05f,0.05f},
                    new float[]{0.05f,0.05f,0.05f,0.05f},
                    new float[]{0.05f,0.05f,0.05f,0.05f},
                    new float[]{0.05f,0.05f,0.05f,0.05f},
                    new int[]{CENTER,CENTER,CENTER,CENTER},
                    new int[]{CENTER,CENTER,CENTER,CENTER},
                    new float[]{0.0f,0.0f,0.0f,0.0f},
                    new int[]{0xff000000,0xff000000,0xff000000,0xff000000},
                    pButtonWidth,
                    pButtonHeight);
            adaptFontSizeToButtonSize(ButtonRenderState.BT_NORMAL);
            adaptFontSizeToButtonSize(ButtonRenderState.BT_HOVERED);
            adaptFontSizeToButtonSize(ButtonRenderState.BT_PRESSED);
            adaptFontSizeToButtonSize(ButtonRenderState.BT_DISABLED);
        }

        public float getEffectiveWidth(int pButtonRenderState) {
            float textPaddingLeft=textPaddingsLeftRelativeToW[pButtonRenderState]*w;
            float textPaddingRight=textPaddingsLeftRelativeToW[pButtonRenderState]*w;
            return w-textPaddingLeft-textPaddingRight;
        }

        public float getEffectiveHeight(int pButtonRenderState) {
            float textPaddingTop=textPaddingsTopRelativeToH[pButtonRenderState]*h;
            float textPaddingBottom=textPaddingsBottomRelativeToH[pButtonRenderState]*h;
            return h-textPaddingTop-textPaddingBottom;
        }

        public void adaptFontSizeToButtonSize(int pButtonRenderState) {
            fontSizes[pButtonRenderState]=min(
                    findFontSizeToFitWidthOrHeight(
                            fonts[pButtonRenderState],
                            fontSizes[pButtonRenderState],
                            getEffectiveWidth(pButtonRenderState),
                            texts[pButtonRenderState],
                            0.01f,
                            true),
                    findFontSizeToFitWidthOrHeight(
                            fonts[pButtonRenderState],
                            fontSizes[pButtonRenderState],
                            getEffectiveHeight(pButtonRenderState),
                            texts[pButtonRenderState],
                            0.01f,
                            false));
        }

        private float getTextWidthOrHeight(PFont pFont,int fontSize,String pText,boolean pWidth) {
            textFont(pFont,fontSize);
            textSize(fontSize);
            if(pWidth)
                return textWidth(pText);
            else {
                int lines=pText.split("\r\n|\r|\n").length;
                // g is current style and only possibility to get hold of textLeading
                return lines*(textDescent() + textAscent())+(lines-1)*g.textLeading;
            }
        }

        private int findFontSizeToFitWidthOrHeight(PFont pFont,int startFontSize,float pWidthOrHeight,String pText,float relativeErrorThreshold,boolean pFitWidth) {
            final int maxIterations=4;
            final int pixelPaddingToFineAdjustCalculationErrors=5; // should be approx. 0
            int fontSize=startFontSize;
            float relativeError=1.0f;
            float textSize=getTextWidthOrHeight(pFont,startFontSize,pText,pFitWidth);
            int iter=0;

            do {
                fontSize=floor(fontSize/textSize*pWidthOrHeight);
                textSize=getTextWidthOrHeight(pFont,fontSize,pText,pFitWidth);
                relativeError=abs(1.0f-textSize/(pWidthOrHeight-pixelPaddingToFineAdjustCalculationErrors));
                ++iter;
            } while(relativeError>relativeErrorThreshold && iter<maxIterations);
            if(textSize>pWidthOrHeight)
                --fontSize;
            return fontSize;
        }

        public void render(float pButtonX,float pButtonY,float pButtonWidth,float pButtonHeight,int pButtonRenderState) {
            super.render(pButtonX,pButtonY,pButtonWidth,pButtonHeight,pButtonRenderState);
            float textPaddingLeft=textPaddingsLeftRelativeToW[pButtonRenderState]*pButtonWidth;
            float textPaddingTop=textPaddingsLeftRelativeToW[pButtonRenderState]*pButtonHeight;
            float x=pButtonX+textPaddingLeft;
            float y=pButtonY+textPaddingTop;
            pushStyle();
            fill(textColors[pButtonRenderState]);
            strokeWeight(textStrokeWeights[pButtonRenderState]);
            stroke(textStrokeColors[pButtonRenderState]);
            textAlign(textHorizontalAlignments[pButtonRenderState],textVerticalAlignments[pButtonRenderState]);
            textFont(fonts[pButtonRenderState],fontSizes[pButtonRenderState]);
            textSize(fontSizes[pButtonRenderState]);
            text(
                    texts[pButtonRenderState],
                    x,
                    y,
                    getEffectiveWidth(pButtonRenderState),
                    getEffectiveHeight(pButtonRenderState));
            popStyle();
        }
    }

    class ImageRectButtonRenderer implements RectButtonRenderer {
        PImage[] images;
        float[] imagePaddingsLeftRelativeToW;
        float[] imagePaddingsTopRelativeToH;
        float[] imagePaddingsRightRelativeToW;
        float[] imagePaddingsBottomRelativeToH;
        int[] imageHorizontalAlignments;
        int[] imageVerticalAlignments;
        float w;
        float h;

        ImageRectButtonRenderer(
                PImage[] pImages,
                float[] pImagePaddingsLeftRelativeToW,
                float[] pImagePaddingsTopRelativeToH,
                float[] pImagePaddingsRightRelativeToW,
                float[] pImagePaddingsBottomRelativeToH,
                int[] pImageHorizontalAlignments,
                int[] pImageVerticalAlignments,
                float pButtonWidth,
                float pButtonHeight) {
            images=pImages.clone(); //creates new array, does not duplicate the images
            imagePaddingsLeftRelativeToW=pImagePaddingsLeftRelativeToW.clone();
            imagePaddingsTopRelativeToH=pImagePaddingsTopRelativeToH.clone();
            imagePaddingsRightRelativeToW=pImagePaddingsRightRelativeToW.clone();
            imagePaddingsBottomRelativeToH=pImagePaddingsBottomRelativeToH.clone();
            imageHorizontalAlignments=pImageHorizontalAlignments.clone();
            imageVerticalAlignments=pImageVerticalAlignments.clone();
            w=pButtonWidth;
            h=pButtonHeight;
            fitImageSizeForAllRenderStates();
        }

        ImageRectButtonRenderer(PImage[] pImages,float pButtonWidth,float pButtonHeight) {
            this(
                    pImages,
                    new float[]{0.0f,0.0f,0.0f,0.0f},
                    new float[]{0.0f,0.0f,0.0f,0.0f},
                    new float[]{0.0f,0.0f,0.0f,0.0f},
                    new float[]{0.0f,0.0f,0.0f,0.0f},
                    new int[]{CENTER,CENTER,CENTER,CENTER},
                    new int[]{CENTER,CENTER,CENTER,CENTER},
                    pButtonWidth,
                    pButtonHeight);
        }

        public void fitImageSize(int pButtonRenderState) {
            float imagePaddingLeft=imagePaddingsLeftRelativeToW[pButtonRenderState]*w;
            float imagePaddingTop=imagePaddingsLeftRelativeToW[pButtonRenderState]*h;
            float imagePaddingRight=imagePaddingsLeftRelativeToW[pButtonRenderState]*w;
            float imagePaddingBottom=imagePaddingsLeftRelativeToW[pButtonRenderState]*h;
            float imgWidth=w-imagePaddingLeft-imagePaddingRight;
            float imgHeight=h-imagePaddingTop-imagePaddingBottom;
            images[pButtonRenderState]=resizeToFitBox(images[pButtonRenderState],floor(imgWidth),floor(imgHeight));
        }

        public void fitImageSizeForAllRenderStates() {
            fitImageSize(ButtonRenderState.BT_NORMAL);
            fitImageSize(ButtonRenderState.BT_HOVERED);
            fitImageSize(ButtonRenderState.BT_PRESSED);
            fitImageSize(ButtonRenderState.BT_DISABLED);
        }

        public void render(float pButtonX,float pButtonY,float pButtonWidth,float pButtonHeight,int pButtonRenderState) {
            float imagePaddingLeft=imagePaddingsLeftRelativeToW[pButtonRenderState]*pButtonWidth;
            float imagePaddingTop=imagePaddingsLeftRelativeToW[pButtonRenderState]*pButtonHeight;
            float imagePaddingRight=imagePaddingsLeftRelativeToW[pButtonRenderState]*pButtonWidth;
            float imagePaddingBottom=imagePaddingsLeftRelativeToW[pButtonRenderState]*pButtonHeight;
            float x=pButtonX+imagePaddingLeft;
            float y=pButtonY+imagePaddingTop;
            float w=pButtonWidth-imagePaddingLeft-imagePaddingRight;
            float h=pButtonHeight-imagePaddingTop-imagePaddingBottom;
            drawImageInBox(images[pButtonRenderState],x,y,w,h,imageHorizontalAlignments[pButtonRenderState],imageVerticalAlignments[pButtonRenderState]);
        }
    }

    class RectButton implements MouseProspect, Drawable {
        float x;
        float y;
        float w;
        float h;

        private boolean isDisabled;
        private boolean isHovered;
        private boolean isPressed;

        boolean isInteractionEnabled;

        int renderState;

        OnRectButtonEventListener eventListener;
        RectButtonRenderer renderer;


        private RectButton(float pX,float pY,float pWidth,float pHeight,OnRectButtonEventListener pEventListener,RectButtonRenderer pRenderer) {
            x=pX;
            y=pY;
            w=pWidth;
            h=pHeight;
            isDisabled=false;
            isHovered=false;
            isPressed=false;
            isInteractionEnabled=true;
            renderState=ButtonRenderState.BT_NORMAL;
            eventListener=pEventListener;
            renderer=pRenderer;
        }

        RectButton(float pX,float pY,float pWidth,float pHeight,OnRectButtonEventListener pEventListener) {
            this(pX,pY,pWidth,pHeight,pEventListener,new RectButtonRendererBase());
        }

        RectButton(String pText,float pX,float pY,float pWidth,float pHeight,OnRectButtonEventListener pEventListener) {
            this(pX,pY,pWidth,pHeight,pEventListener,new TextRectButtonRenderer(pText, pWidth, pHeight));
        }

        RectButton(PImage[] pImages,float pX,float pY,float pWidth,float pHeight,OnRectButtonEventListener pEventListener) {
            this(pX,pY,pWidth,pHeight,pEventListener,new ImageRectButtonRenderer(pImages,pWidth,pHeight));
        }

        public void setDisabled(boolean pIsDisabled) {
            isDisabled=pIsDisabled;
            if(pIsDisabled)
                renderState=ButtonRenderState.BT_DISABLED;
            else
                renderState=ButtonRenderState.BT_NORMAL;
        }

        public boolean isDisabled() {
            return isDisabled;
        }

        public boolean isHovered() {
            return isHovered;
        }

        public boolean isPressed() {
            return isPressed;
        }

        public void draw() {
            if(isInteractionEnabled)
                handleMouseProspect(this);
            if(renderer!=null)
                renderer.render(x,y,w,h,renderState);
        }

        // implements MouseProspect

        public boolean isInside(float pX,float pY) {
            return pX >= x && pX <= x+w && pY >= y && pY <= y+h;
        }

        public void notifyHovered() {
            boolean _isHovered=isHovered;
            isHovered=true;
            if(!_isHovered)
                if(eventListener!=null)
                    eventListener.onMouseOver(this);
            if(!isDisabled)
                renderState=ButtonRenderState.BT_HOVERED;
        }

        public void notifyNotHovered() {
            boolean _isHovered=isHovered;
            isHovered=false;
            if(_isHovered)
                if(eventListener!=null)
                    eventListener.onMouseOut(this);
            boolean _isPressed=isPressed;
            isPressed=false;
            if(_isPressed)
                if(eventListener!=null)
                    eventListener.onMouseReleased(this);
            if(!isDisabled)
                renderState=ButtonRenderState.BT_NORMAL;
        }

        public void notifyPressed() {
            boolean _isPressed=isPressed;
            isPressed=true;
            isHovered=true;
            if(!_isPressed)
                if(eventListener!=null)
                    eventListener.onMousePressed(this);
            if(!isDisabled)
                renderState=ButtonRenderState.BT_PRESSED;
        }

        public void notifyReleased() {
            boolean _isPressed=isPressed;
            isPressed=false;
            isHovered=true;
            if(_isPressed) {
                if(eventListener!=null)
                    eventListener.onMouseReleased(this);
                if(!isDisabled)
                    if(eventListener!=null)
                        eventListener.onMouseClicked(this);
            }
            if(!isDisabled)
                renderState=ButtonRenderState.BT_HOVERED;
        }
    }
    float width_meter;
    float height_meter;
    float metersToPixelsScaleFactor;
    float pixelsToMetersScaleFactor;

    public void initMetersToPixelsConversion(final float pMinShownMeters) {
        metersToPixelsScaleFactor = min(width,height)/pMinShownMeters;
        pixelsToMetersScaleFactor = 1/metersToPixelsScaleFactor;
        width_meter = pixelsToMeters(width);
        height_meter = pixelsToMeters(height);
    }

    public float pixelsToMeters(int pixels) {
        return pixels*pixelsToMetersScaleFactor;
    }

    public int metersToPixels(float meters) {
        return floor(meters*metersToPixelsScaleFactor);
    }
    /** Provides a method that should be called every frame
     */
    static interface Drawable {
        public void draw();
    }


    /** The resize function of Processing crashes on Android
     *  for all Images that where not created by loadImage(),
     *  but by calls to get(), copy(), etc. This is a workaround.
     */
    public PImage resize(PImage pImage,int pWidth,int pHeight) {
        if(pHeight==0) {
            pHeight=pImage.height*pWidth/pImage.width;
        } else if(pWidth==0) {
            pWidth=pImage.width*pHeight/pImage.height;
        }
        if(pWidth==pImage.width && pHeight==pImage.height)
            return pImage;
        PImage img=createImage(pWidth,pHeight,ARGB);
        img.copy(pImage,0,0,pImage.width,pImage.height,0,0,pWidth,pHeight);
        return img;
    }

    public PImage resizeToFitBox(PImage pImage,int pWidth,int pHeight) {
        float scaleFactorW=(float)pWidth/pImage.width;
        float scaleFactorH=(float)pHeight/pImage.height;
        if(scaleFactorW>scaleFactorH)
            return resize(pImage,0,floor(pHeight));
        else
            return resize(pImage,floor(pWidth),0);
    }

    public PImage resizeToCoverBox(PImage pImage,int pWidth,int pHeight) {
        boolean isWidthFitted=((float)pWidth/pImage.width)*pImage.height>=pHeight;
        if(isWidthFitted)
            return resize(pImage,pWidth,0);
        else
            return resize(pImage,0,pHeight);
    }

    public void drawImageInBox(
            PImage pImage,
            float pX,
            float pY,
            float pWidth,
            float pHeight,
            int pHorizontalAlignment,
            int pVerticalAlignment) {

        float imageX=0;
        float imageY=0;
        switch(pHorizontalAlignment) {
            default:
            case LEFT:
                imageX=pX;
                break;
            case CENTER:
                imageX=pX+0.5f*(pWidth-pImage.width);
                break;
            case RIGHT:
                imageX=pX+pWidth-pImage.width;
                break;
        }

        switch(pVerticalAlignment) {
            default:
            case TOP:
                imageY=pY;
                break;
            case CENTER:
                imageY=pY+0.5f*(pHeight-pImage.height);
                break;
            case BOTTOM:
                imageY=pY+pHeight-pImage.height;
                break;
        }
        image(pImage,imageX,imageY);
    }

    public float getTextWidthOrHeight(PFont pFont,int fontSize,String pText,boolean pWidth) {
        textFont(pFont,fontSize);
        textSize(fontSize);
        if(pWidth)
            return textWidth(pText);
        else {
            int lines=pText.split("\r\n|\r|\n").length;
            // g is current style and only possibility to get hold of textLeading
            return lines*(textDescent() + textAscent())+(lines-1)*g.textLeading;
        }
    }

    public int findFontSizeToFitWidthOrHeight(PFont pFont,int startFontSize,float pWidthOrHeight,String pText,float relativeErrorThreshold,boolean pFitWidth) {
        final int maxIterations=4;
        final int pixelPaddingToFineAdjustCalculationErrors=5; // should be approx. 0
        int fontSize=startFontSize;
        float relativeError=1.0f;
        float textSize=getTextWidthOrHeight(pFont,startFontSize,pText,pFitWidth);
        int iter=0;

        do {
            fontSize=floor(fontSize/textSize*pWidthOrHeight);
            textSize=getTextWidthOrHeight(pFont,fontSize,pText,pFitWidth);
            relativeError=abs(1.0f-textSize/(pWidthOrHeight-pixelPaddingToFineAdjustCalculationErrors));
            ++iter;
        } while(relativeError>relativeErrorThreshold && iter<maxIterations);
        if(textSize>pWidthOrHeight)
            --fontSize;
        return fontSize;
    }

    public int findFontSizeToFitBox(PFont pFont,int startFontSize,float pWidth, float pHeight, String pText,float relativeErrorThreshold) {
        return min(
                findFontSizeToFitWidthOrHeight(pFont, startFontSize, pWidth, pText, relativeErrorThreshold, true),
                findFontSizeToFitWidthOrHeight(pFont, startFontSize, pHeight, pText, relativeErrorThreshold, false)
        );
    }

    public int findFontSizeToFitBox(float pWidth, float pHeight, String pText) {
        int startFontSize=12;
        float relativeErrorThreshold = 0.01f;
        PFont font = createDefaultFont(startFontSize);
        return min(
                findFontSizeToFitWidthOrHeight(font, startFontSize, pWidth, pText, relativeErrorThreshold, true),
                findFontSizeToFitWidthOrHeight(font, startFontSize, pHeight, pText, relativeErrorThreshold, false)
        );
    }
    class VisualizationState extends State implements OnZoomChangedListener {

        private Hud hud;
        private BackgroundGrid backgroundGrid;
        private Figurine figurine;
        private boolean isSensorAccuracyLow;
        private int lowSensorAccuracyBoxH;
        private PFont font;
        private int textS;
        private String lowSensorAccuracyText;

        public void setIsSensorAccuracyLow(boolean pIsSensorAccuracyLow) {
            isSensorAccuracyLow=pIsSensorAccuracyLow;
        }

        public VisualizationState() {
            super("VISUALIZATION_STATE");
            backgroundGrid = new BackgroundGrid(width,height);
            hud = new Hud(width,height,this);
            figurine = new Figurine();
            isSensorAccuracyLow=false;
            lowSensorAccuracyText = "Sensor Precision changed to low!";
            lowSensorAccuracyBoxH=floor(0.2f*height);
            textS = findFontSizeToFitBox((float)width, (float)lowSensorAccuracyBoxH, lowSensorAccuracyText);
            font = createDefaultFont(textS);
        }

        public void draw() {
            background(0xff1da1f2);
            backgroundGrid.draw();
            pushMatrix();
            translate(backgroundGrid.offsetX_origin_p, backgroundGrid.offsetY_origin_p);
            int x0=0,y0=0;
            strokeWeight(0.01f*width);
            stroke(0xff000000);
            if(path!=null) {
                for(PVector position : path) {
                    int x1=metersToPixels(position.x);
                    int y1=metersToPixels(position.y);
                    line(x0,y0,x1,y1);
                    x0=x1;
                    y0=y1;
                }
            }

            pushMatrix();
            translate(x0,y0);
            pushMatrix();
            rotate(direction);
            figurine.draw();
            popMatrix();
            popMatrix();

            popMatrix();
            hud.draw();
            super.draw();


            if(isSensorAccuracyLow) {
                pushStyle();
                rectMode(CENTER);
                fill(0x99000000);
                rect(width/2, height/2, width, lowSensorAccuracyBoxH);
                fill(0xffFF0000);
                textFont(font, textS);
                textSize(textS);
                textAlign(CENTER, CENTER);
                text(lowSensorAccuracyText, width/2, height/2, width, lowSensorAccuracyBoxH);
                popStyle();
            }
        }

        public void onZoomChanged(float pMinShownMeters) {
            initMetersToPixelsConversion(pMinShownMeters);
            backgroundGrid.calculateParameters();
        }

        public void OnAppear() {
            if(androidInterface!=null)
                androidInterface.onReset();
        }

        public void onLowSensorAccuracy() {
            isSensorAccuracyLow=true;
        }

        public void reset() {
            setIsSensorAccuracyLow(false);
            hud.resetToInitialZoom();
        }
    }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "ProcessingVisualization" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
