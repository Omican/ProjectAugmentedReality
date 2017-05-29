package com.par.projectaugmentedreality;

/**
 * Created by Maick on 5/3/2017.
 */


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.par.projectaugmentedreality.VideoPlayback.KeyFrameShaders;
import com.par.projectaugmentedreality.VideoPlayback.VideoPlayback;
import com.par.projectaugmentedreality.VideoPlayback.VideoPlaybackShaders;
import com.par.projectaugmentedreality.VideoPlayback.VideoPlayerHelper;
import com.vuforia.Device;
import com.vuforia.ImageTarget;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.TargetFinder;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.TrackerManager;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.vuforia.Vec3F;
import com.vuforia.Vuforia;
import com.par.projectaugmentedreality.AppRenderer;
import com.par.projectaugmentedreality.AppRendererControl;
import com.par.projectaugmentedreality.ApplicationSession;
import com.par.projectaugmentedreality.utils.CubeShaders;
import com.par.projectaugmentedreality.utils.Utils;
import com.par.projectaugmentedreality.utils.Teapot;
import com.par.projectaugmentedreality.utils.Texture;
import com.par.projectaugmentedreality.VideoPlayback.VideoPlayback;


// The renderer class for the CloudReco .
public class CloudRecoRenderer implements GLSurfaceView.Renderer, AppRendererControl
{
    private ApplicationSession vuforiaAppSession;
    private AppRenderer mAppRenderer;
    private String type;
    private String URL;
    ImageTarget imageTarget;
    String trackableName;
    boolean startedIntent;
    private Context context;
    private ArrayList<String> imageTargetList = new ArrayList<>();
    private String isQuizTarget;

    private DatabaseReference mDatabase;

    private static final float OBJECT_SCALE_FLOAT = 3.0f;

    private int shaderProgramID;
    private int vertexHandle;
    private int textureCoordHandle;
    private int mvpMatrixHandle;
    private int texr2DHandle;

    // Video Playback Rendering Specific
    private int videoPlaybackShaderID = 0;
    private int videoPlaybackVertexHandle = 0;
    private int videoPlaybackTexCoordHandle = 0;
    private int videoPlaybackMVPMatrixHandle = 0;
    private int videoPlaybackTexrOESHandle = 0;

    static int NUM_QUAD_VERTEX = 4;
    static int NUM_QUAD_INDEX = 6;

    private Matrix44F tappingProjectionMatrix = null;
    // Video Playback Textures for the two targets
    int videoPlaybackTextureID[] = new int[CloudReco.NUM_TARGETS];

    private float videoQuadTextureCoordsTransformedStones[] = { 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };

    private float videoQuadTextureCoordsTransformedChips[] = { 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };

    // Keyframe and icon rendering specific
    private int keyframeShaderID = 0;
    private int keyframeVertexHandle = 0;
    private int keyframeTexCoordHandle = 0;
    private int keyframeMVPMatrixHandle = 0;
    private int keyframeTexr2DHandle = 0;

    private float[][] mTexCoordTransformationMatrix = null;
    private VideoPlayerHelper mVideoPlayerHelper[] = null;
    private String mMovieName[] = null;
    private VideoPlayerHelper.MEDIA_TYPE mCanRequestType[] = null;
    private int mSeekPosition[] = null;
    private boolean mShouldPlayImmediately[] = null;
    private long mLostTrackingSince[] = null;
    private boolean mLoadRequested[] = null;

    Buffer quadVertices, quadTexCoords, quadIndices, quadNormals;

    double quadVerticesArray[] = { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, -1.0f, 1.0f, 0.0f };

    double quadTexCoordsArray[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            1.0f };

    double quadNormalsArray[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, };

    short quadIndicesArray[] = { 0, 1, 2, 2, 3, 0 };

    private Vector<Texture> mTextures;

    private Teapot mTeapot;

    private CloudReco mActivity;

    private boolean mIsActive = false;

    Matrix44F modelViewMatrix[] = new Matrix44F[CloudReco.NUM_TARGETS];
    boolean isTracking[] = new boolean[CloudReco.NUM_TARGETS];
    VideoPlayerHelper.MEDIA_STATE currentStatus[] = new VideoPlayerHelper.MEDIA_STATE[CloudReco.NUM_TARGETS];

    // These hold the aspect ratio of both the video and the
    // keyframe
    float videoQuadAspectRatio[] = new float[CloudReco.NUM_TARGETS];
    float keyframeQuadAspectRatio[] = new float[CloudReco.NUM_TARGETS];

    Vec3F targetPositiveDimensions[] = new Vec3F[CloudReco.NUM_TARGETS];

    public CloudRecoRenderer(ApplicationSession session, CloudReco activity, Context context)
    {
        this.context = context;
        vuforiaAppSession = session;
        mActivity = activity;

        // AppRenderer used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mAppRenderer = new AppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);

        // Create an array of the size of the number of targets we have
        mVideoPlayerHelper = new VideoPlayerHelper[CloudReco.NUM_TARGETS];
        mMovieName = new String[CloudReco.NUM_TARGETS];
        mCanRequestType = new VideoPlayerHelper.MEDIA_TYPE[CloudReco.NUM_TARGETS];
        mSeekPosition = new int[CloudReco.NUM_TARGETS];
        mShouldPlayImmediately = new boolean[CloudReco.NUM_TARGETS];
        mLostTrackingSince = new long[CloudReco.NUM_TARGETS];
        mLoadRequested = new boolean[CloudReco.NUM_TARGETS];
        mTexCoordTransformationMatrix = new float[CloudReco.NUM_TARGETS][16];

        // Initialize the arrays to default values
        for (int i = 0; i < CloudReco.NUM_TARGETS; i++)
        {
            mVideoPlayerHelper[i] = null;
            mMovieName[i] = "";
            mCanRequestType[i] = VideoPlayerHelper.MEDIA_TYPE.ON_TEXTURE_FULLSCREEN;
            mSeekPosition[i] = 0;
            mShouldPlayImmediately[i] = false;
            mLostTrackingSince[i] = -1;
            mLoadRequested[i] = false;
        }

        for (int i = 0; i < CloudReco.NUM_TARGETS; i++)
            targetPositiveDimensions[i] = new Vec3F();

        for (int i = 0; i < CloudReco.NUM_TARGETS; i++)
            modelViewMatrix[i] = new Matrix44F();
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
       // mActivity.startFinderIfStopped();
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mAppRenderer.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        //mActivity.startFinderIfStopped();
        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mAppRenderer.onConfigurationChanged(mIsActive);

        // Call function to initialize rendering:
        initRendering();
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        // Call our function to render content from AppRenderer class
        mAppRenderer.render();
    }


    public void setActive(boolean active)
    {
        mIsActive = active;

        if(mIsActive)
            mAppRenderer.configureVideoBackground();
    }


    // Function for initializing the renderer.
    private void initRendering()
    {
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);

        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, t.mData);

       /*     GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, t.mData);*/
        }

        for (int i = 0; i < CloudReco.NUM_TARGETS; i++)
        {
            GLES20.glGenTextures(1, videoPlaybackTextureID, i);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    videoPlaybackTextureID[i]);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        }

        videoPlaybackShaderID = Utils.createProgramFromShaderSrc(
                VideoPlaybackShaders.VIDEO_PLAYBACK_VERTEX_SHADER,
                VideoPlaybackShaders.VIDEO_PLAYBACK_FRAGMENT_SHADER);
        videoPlaybackVertexHandle = GLES20.glGetAttribLocation(
                videoPlaybackShaderID, "vertexPosition");
        videoPlaybackTexCoordHandle = GLES20.glGetAttribLocation(
                videoPlaybackShaderID, "vertexTexCoord");
        videoPlaybackMVPMatrixHandle = GLES20.glGetUniformLocation(
                videoPlaybackShaderID, "modelViewProjectionMatrix");
        videoPlaybackTexrOESHandle = GLES20.glGetUniformLocation(
                videoPlaybackShaderID, "texrOES");


        // This is a simpler shader with regular 2D textures
        keyframeShaderID = Utils.createProgramFromShaderSrc(
                KeyFrameShaders.KEY_FRAME_VERTEX_SHADER,
                KeyFrameShaders.KEY_FRAME_FRAGMENT_SHADER);
        keyframeVertexHandle = GLES20.glGetAttribLocation(keyframeShaderID,
                "vertexPosition");
        keyframeTexCoordHandle = GLES20.glGetAttribLocation(keyframeShaderID,
                "vertexTexCoord");
        keyframeMVPMatrixHandle = GLES20.glGetUniformLocation(keyframeShaderID,
                "modelViewProjectionMatrix");
        keyframeTexr2DHandle = GLES20.glGetUniformLocation(
                keyframeShaderID, "texr2D");

        keyframeQuadAspectRatio[CloudReco.STONES] = (float) mTextures
                .get(0).mHeight / (float) mTextures.get(0).mWidth;
        keyframeQuadAspectRatio[CloudReco.CHIPS] = (float) mTextures.get(1).mHeight
                / (float) mTextures.get(1).mWidth;

        quadVertices = fillBuffer(quadVerticesArray);
        quadTexCoords = fillBuffer(quadTexCoordsArray);
        quadIndices = fillBuffer(quadIndicesArray);
        quadNormals = fillBuffer(quadNormalsArray);

        shaderProgramID = Utils.createProgramFromShaderSrc(
                CubeShaders.CUBE_MESH_VERTEX_SHADER,
                CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexPosition");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "modelViewProjectionMatrix");
        texr2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "texr2D");
        mTeapot = new Teapot();
    }

    private Buffer fillBuffer(double[] array)
    {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each float takes 4 bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (double d : array)
            bb.putFloat((float) d);
        bb.rewind();

        return bb;

    }

    private Buffer fillBuffer(short[] array)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(2 * array.length); // each
        // short
        // takes 2
        // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (short s : array)
            bb.putShort(s);
        bb.rewind();

        return bb;

    }

    private Buffer fillBuffer(float[] array)
    {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each float takes 4 bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array)
            bb.putFloat(d);
        bb.rewind();

        return bb;

    }


    // The render function.
    // The render function called from AppRendering by using RenderingPrimitives views.
    // The state is owned by AppRenderer which is controlling it's lifecycle.
    // State should not be cached outside this method.
    public void renderFrame(State state, float[] projectionMatrix) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Renders video background replacing Renderer.DrawVideoBackground()
        mAppRenderer.renderVideoBackground();


        //mActivity.startFinderIfStopped();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        if (tappingProjectionMatrix == null) {
            tappingProjectionMatrix = new Matrix44F();
            tappingProjectionMatrix.setData(projectionMatrix);
        }

        float temp[] = {0.0f, 0.0f, 0.0f};
        for (int i = 0; i < CloudReco.NUM_TARGETS; i++) {
            isTracking[i] = false;
            targetPositiveDimensions[i].setData(temp);
        }

        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            startedIntent = false;
            TrackableResult trackableResult = state.getTrackableResult(tIdx);
            mActivity.startFinderIfStopped();

            imageTarget = (ImageTarget) trackableResult.getTrackable();
            int currentTarget;

            mDatabase.child(imageTarget.getName()).child("isQuizTarget").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    isQuizTarget = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (isQuizTarget != null && isQuizTarget.equals("false")) {
                mDatabase.child(imageTarget.getName()).child("type").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        type = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mDatabase.child(imageTarget.getName()).child("videoUrl").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        URL = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            // We store the modelview matrix to be used later by the tap
            // calculation
            if (isQuizTarget != null && isQuizTarget.equals("true")) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, QuizScreen.class);
                        intent.putStringArrayListExtra("ImageTargets", imageTargetList);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (startedIntent == false) {
                            mActivity.stopFinderIfStarted();
                            context.startActivity(intent);
                            startedIntent = true;
                        }
                    }
                }, 800);
            } if (type != null && type.equals("text") && !imageTarget.getName().isEmpty()) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        trackableName = imageTarget.getName();
                        Intent intent = new Intent(context, TargetInformation.class);
                        intent.putExtra("Dataset", trackableName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (startedIntent == false) {
                            mActivity.stopFinderIfStarted();
                            context.startActivity(intent);
                            startedIntent = true;
                        }

                    }
                }, 800);
            } if (type != null && type.equals("video")) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, FullscreenActivity.class);
                        intent.putExtra("VideoURL", URL);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (startedIntent == false) {
                            mActivity.stopFinderIfStarted();
                            context.startActivity(intent);
                            startedIntent = true;
                        }
                    }
              /*  currentTarget = CloudReco.CHIPS;


                modelViewMatrix[currentTarget] = Tool
                        .convertPose2GLMatrix(trackableResult.getPose());

                isTracking[currentTarget] = true;

                targetPositiveDimensions[currentTarget] = imageTarget.getSize();

                // The pose delivers the center of the target, thus the dimensions
                // go from -width/2 to width/2, same for height
                temp[0] = targetPositiveDimensions[currentTarget].getData()[0] / 2.0f;
                temp[1] = targetPositiveDimensions[currentTarget].getData()[1] / 2.0f;
                targetPositiveDimensions[currentTarget].setData(temp);

                // If the movie is ready to start playing or it has reached the end
                // of playback we render the keyframe
                if ((currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.READY)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.REACHED_END)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.NOT_READY)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.ERROR)) {
                    float[] modelViewMatrixKeyframe = Tool.convertPose2GLMatrix(
                            trackableResult.getPose()).getData();
                    float[] modelViewProjectionKeyframe = new float[16];
                    // Matrix.translateM(modelViewMatrixKeyframe, 0, 0.0f, 0.0f,
                    // targetPositiveDimensions[currentTarget].getData()[0]);

                    // Here we use the aspect ratio of the keyframe since it
                    // is likely that it is not a perfect square

                    float ratio = 1.0f;
                    if (mTextures.get(currentTarget).mSuccess)
                        ratio = keyframeQuadAspectRatio[currentTarget];
                    else
                        ratio = targetPositiveDimensions[currentTarget].getData()[1]
                                / targetPositiveDimensions[currentTarget].getData()[0];

                    Matrix.scaleM(modelViewMatrixKeyframe, 0,
                            targetPositiveDimensions[currentTarget].getData()[0],
                            targetPositiveDimensions[currentTarget].getData()[0]
                                    * ratio,
                            targetPositiveDimensions[currentTarget].getData()[0]);
                    Matrix.multiplyMM(modelViewProjectionKeyframe, 0,
                            projectionMatrix, 0, modelViewMatrixKeyframe, 0);

                    GLES20.glUseProgram(keyframeShaderID);

                    // Prepare for rendering the keyframe
                    GLES20.glVertexAttribPointer(keyframeVertexHandle, 3,
                            GLES20.GL_FLOAT, false, 0, quadVertices);
                    GLES20.glVertexAttribPointer(keyframeTexCoordHandle, 2,
                            GLES20.GL_FLOAT, false, 0, quadTexCoords);

                    GLES20.glEnableVertexAttribArray(keyframeVertexHandle);
                    GLES20.glEnableVertexAttribArray(keyframeTexCoordHandle);

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                    // The first loaded texture from the assets folder is the
                    // keyframe
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                            mTextures.get(currentTarget).mTextureID[0]);
                    GLES20.glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, false,
                            modelViewProjectionKeyframe, 0);
                    GLES20.glUniform1i(keyframeTexr2DHandle, 0);

                    // Render
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, NUM_QUAD_INDEX,
                            GLES20.GL_UNSIGNED_SHORT, quadIndices);

                    GLES20.glDisableVertexAttribArray(keyframeVertexHandle);
                    GLES20.glDisableVertexAttribArray(keyframeTexCoordHandle);

                    GLES20.glUseProgram(0);
                } else
                // In any other case, such as playing or paused, we render
                // the actual contents
                {
                    float[] modelViewMatrixVideo = Tool.convertPose2GLMatrix(
                            trackableResult.getPose()).getData();
                    float[] modelViewProjectionVideo = new float[16];
                    // Matrix.translateM(modelViewMatrixVideo, 0, 0.0f, 0.0f,
                    // targetPositiveDimensions[currentTarget].getData()[0]);

                    // Here we use the aspect ratio of the video frame
                    Matrix.scaleM(modelViewMatrixVideo, 0,
                            targetPositiveDimensions[currentTarget].getData()[0],
                            targetPositiveDimensions[currentTarget].getData()[0]
                                    * videoQuadAspectRatio[currentTarget],
                            targetPositiveDimensions[currentTarget].getData()[0]);
                    Matrix.multiplyMM(modelViewProjectionVideo, 0,
                            projectionMatrix, 0, modelViewMatrixVideo, 0);

                    GLES20.glUseProgram(videoPlaybackShaderID);

                    // Prepare for rendering the keyframe
                    GLES20.glVertexAttribPointer(videoPlaybackVertexHandle, 3,
                            GLES20.GL_FLOAT, false, 0, quadVertices);

                    if (imageTarget.getName().compareTo("stones") == 0)
                        GLES20.glVertexAttribPointer(videoPlaybackTexCoordHandle,
                                2, GLES20.GL_FLOAT, false, 0,
                                fillBuffer(videoQuadTextureCoordsTransformedStones));
                    else
                        GLES20.glVertexAttribPointer(videoPlaybackTexCoordHandle,
                                2, GLES20.GL_FLOAT, false, 0,
                                fillBuffer(videoQuadTextureCoordsTransformedChips));

                    GLES20.glEnableVertexAttribArray(videoPlaybackVertexHandle);
                    GLES20.glEnableVertexAttribArray(videoPlaybackTexCoordHandle);

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                    // IMPORTANT:
                    // Notice here that the texture that we are binding is not the
                    // typical GL_TEXTURE_2D but instead the GL_TEXTURE_EXTERNAL_OES
                    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                            videoPlaybackTextureID[currentTarget]);
                    GLES20.glUniformMatrix4fv(videoPlaybackMVPMatrixHandle, 1,
                            false, modelViewProjectionVideo, 0);
                    GLES20.glUniform1i(videoPlaybackTexrOESHandle, 0);

                    // Render
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, NUM_QUAD_INDEX,
                            GLES20.GL_UNSIGNED_SHORT, quadIndices);

                    GLES20.glDisableVertexAttribArray(videoPlaybackVertexHandle);
                    GLES20.glDisableVertexAttribArray(videoPlaybackTexCoordHandle);

                    GLES20.glUseProgram(0);

                }

                // The following section renders the icons. The actual textures used
                // are loaded from the assets folder

                if ((currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.READY)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.REACHED_END)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.PAUSED)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.NOT_READY)
                        || (currentStatus[currentTarget] == VideoPlayerHelper.MEDIA_STATE.ERROR)) {
                    // If the movie is ready to be played, pause, has reached end or
                    // is not
                    // ready then we display one of the icons
                    float[] modelViewMatrixButton = Tool.convertPose2GLMatrix(
                            trackableResult.getPose()).getData();
                    float[] modelViewProjectionButton = new float[16];

                    GLES20.glDepthFunc(GLES20.GL_LEQUAL);

                    GLES20.glEnable(GLES20.GL_BLEND);
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
                            GLES20.GL_ONE_MINUS_SRC_ALPHA);

                    // The inacuracy of the rendering process in some devices means
                    // that
                    // even if we use the "Less or Equal" version of the depth
                    // function
                    // it is likely that we will get ugly artifacts
                    // That is the translation in the Z direction is slightly
                    // different
                    // Another posibility would be to use a depth func "ALWAYS" but
                    // that is typically not a good idea
                    Matrix
                            .translateM(
                                    modelViewMatrixButton,
                                    0,
                                    0.0f,
                                    0.0f,
                                    targetPositiveDimensions[currentTarget].getData()[1] / 10.98f);
                    Matrix
                            .scaleM(
                                    modelViewMatrixButton,
                                    0,
                                    (targetPositiveDimensions[currentTarget].getData()[1] / 2.0f),
                                    (targetPositiveDimensions[currentTarget].getData()[1] / 2.0f),
                                    (targetPositiveDimensions[currentTarget].getData()[1] / 2.0f));
                    Matrix.multiplyMM(modelViewProjectionButton, 0,
                            projectionMatrix, 0, modelViewMatrixButton, 0);

                    GLES20.glUseProgram(keyframeShaderID);

                    GLES20.glVertexAttribPointer(keyframeVertexHandle, 3,
                            GLES20.GL_FLOAT, false, 0, quadVertices);
                    GLES20.glVertexAttribPointer(keyframeTexCoordHandle, 2,
                            GLES20.GL_FLOAT, false, 0, quadTexCoords);

                    GLES20.glEnableVertexAttribArray(keyframeVertexHandle);
                    GLES20.glEnableVertexAttribArray(keyframeTexCoordHandle);

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                    // Depending on the status in which we are we choose the
                    // appropriate
                    // texture to display. Notice that unlike the video these are
                    // regular
                    // GL_TEXTURE_2D textures
                    switch (currentStatus[currentTarget]) {
                        case READY:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                    mTextures.get(2).mTextureID[0]);
                            break;
                        case REACHED_END:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                    mTextures.get(2).mTextureID[0]);
                            break;
                        case PAUSED:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                    mTextures.get(2).mTextureID[0]);
                            break;
                        case NOT_READY:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                    mTextures.get(3).mTextureID[0]);
                            break;
                        case ERROR:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                    mTextures.get(4).mTextureID[0]);
                            break;
                        default:
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                    mTextures.get(3).mTextureID[0]);
                            break;
                    }
                    GLES20.glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, false,
                            modelViewProjectionButton, 0);
                    GLES20.glUniform1i(keyframeTexr2DHandle, 0);

                    // Render
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, NUM_QUAD_INDEX,
                            GLES20.GL_UNSIGNED_SHORT, quadIndices);

                    GLES20.glDisableVertexAttribArray(keyframeVertexHandle);
                    GLES20.glDisableVertexAttribArray(keyframeTexCoordHandle);

                    GLES20.glUseProgram(0);

                    // Finally we return the depth func to its original state
                    GLES20.glDepthFunc(GLES20.GL_LESS);
                    GLES20.glDisable(GLES20.GL_BLEND);
                }

                Utils.checkGLError("VideoPlayback renderFrame");
            }
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Renderer.getInstance().end();*/

                }, 800);
            }
        }
    }





    private void renderAugmentation(TrackableResult trackableResult, float[] projectionMatrix)
    {
        Matrix44F modelViewMatrix_Vuforia = Tool
                .convertPose2GLMatrix(trackableResult.getPose());
        float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

        int textureIndex = 0;

        // deal with the modelview and projection matrices
        float[] modelViewProjection = new float[16];
        Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, OBJECT_SCALE_FLOAT);
        Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT,
                OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
        Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

        // activate the shader program and bind the vertex/normal/tex coords
        GLES20.glUseProgram(shaderProgramID);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false,
                0, mTeapot.getVertices());
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT,
                false, 0, mTeapot.getTexCoords());

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        // activate texture 0, bind it, and pass to shader
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                mTextures.get(textureIndex).mTextureID[0]);
        GLES20.glUniform1i(texr2DHandle, 0);

        // pass the model view matrix to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                modelViewProjection, 0);

        // finally draw the teapot
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mTeapot.getNumObjectIndex(),
                GLES20.GL_UNSIGNED_SHORT, mTeapot.getIndices());

        // disable the enabled arrays
        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);

        Utils.checkGLError("CloudReco renderFrame");
    }


    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
    }

}
