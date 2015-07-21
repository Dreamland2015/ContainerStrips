import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import heronarts.lx.*; 
import heronarts.lx.audio.*; 
import heronarts.lx.color.*; 
import heronarts.lx.model.*; 
import heronarts.lx.modulator.*; 
import heronarts.lx.output.*; 
import heronarts.lx.parameter.*; 
import heronarts.lx.pattern.*; 
import heronarts.lx.transition.*; 
import heronarts.lx.transform.*; 
import heronarts.p2lx.*; 
import heronarts.p2lx.ui.*; 
import heronarts.p2lx.ui.control.*; 
import ddf.minim.*; 
import processing.opengl.*; 
import java.util.*; 

import heronarts.p2lx.font.*; 
import heronarts.lx.transition.*; 
import heronarts.lx.transform.*; 
import heronarts.p2lx.ui.component.*; 
import heronarts.lx.pattern.*; 
import heronarts.lx.model.*; 
import heronarts.p2lx.*; 
import heronarts.lx.midi.device.*; 
import heronarts.p2lx.ui.control.*; 
import heronarts.lx.modulator.*; 
import heronarts.lx.output.*; 
import heronarts.lx.midi.*; 
import heronarts.lx.effect.*; 
import heronarts.lx.color.*; 
import heronarts.lx.parameter.*; 
import heronarts.p2lx.video.*; 
import heronarts.p2lx.ui.*; 
import heronarts.lx.*; 
import heronarts.lx.audio.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ContainerStrip extends PApplet {

// Get all our imports out of the way


















// Let's work in feet and inches
final static int INCHES = 1;
final static int FEET = 12 * INCHES;

// Top-level, we have a model and a P2LX instance
Model model;

P2LX lx;

// Setup establishes the windowing and LX constructs
public void setup() 
{
	// Dimension of the window
	size(800, 600, OPENGL); 

    model = new Model();

	// Create the P2LX engine for Dreamland
	lx = new P2LX(this, model);


	// Patterns setup
	lx.setPatterns(new LXPattern[] 
	{
		new LayerDemoPattern(lx),
		new IteratorTestPattern(lx),
		new SolidColorPattern(lx, 100),
		new TestHuePattern(lx),
		new TestXPattern(lx),
		new TestYPattern(lx),
		new TestZPattern(lx),
		new TestProjectionPattern(lx)
	});


	// Add pieces of the UI
	lx.ui.addLayer(
		// Add a 3D scene with a camera. Mouse movements control the camera
		new UI3dContext(lx.ui){}

		// Look at center of model
		.setCenter(model.cx, model.cy, model.cz)

		// Position ourself some distance away from model
		.setRadius(40 * FEET)

		// Set how fast the UI can rotate rad/s
		.setRotateVelocity(12 * PI)

		// Set how fast the UI can rotational acceleration
		.setRotateAcceleration(3 * PI)

		// Add a point cloud representing the LEDs
		.addComponent(new UIPointCloud(lx,model).setPointWeight(3))
	);

	// Basic 2-D contorol for channel with draggable windows
	
	// Grabs channel 0
	lx.ui.addLayer(new UIChannelControl(lx.ui, lx.engine.getChannel(0), 4, 4)); 
	
	// Threaded control and FPS
	lx.ui.addLayer(new UIEngineControl(lx.ui, 4, 326)); 
	
	// Various sliders, knobs and buttons
	lx.ui.addLayer(new UIComponentsDemo(lx.ui, width-144, 4)); 

	buildOutputs();
}

public void draw() 
{
    background(0xff292929);
}


static class Model extends LXModel 
{
  public LXVector v1 = new LXVector(new LXPoint(1,1,0));
  
  public Model() 
  {
    super(new Fixture());
  }
  
  private static class Fixture extends LXAbstractFixture 
  {
    
    private static final int z = 0;
    private static final int OFFSET = FEET;
    private static final int NUMBER_OF_LEDS = 25;
    
    private Fixture() 
    {
      // Here's the core loop where we generate the positions
      // of the points in our model
      // for (int ledPoint = 0; ledPoint < 8; ++ledPoint) 
      for (int n = 0; n < NUMBER_OF_LEDS; ++n)
      {


        addPoint(new LXPoint(n * FEET, 0, 0));
      }
    }
  }
}

// public static class Lamppost extends LXModel 
// {
//   public final static int LIGHTS_PER_STRIP = 30; 

//   public final float x;
//   public final float y;
//   public final float z;
  

//   public lamppost(float x, float y, float z) 
//   {
//     super(new Fixture(x, y, z));
//     // Fixture fixture = (Fixture) this.fixtures.get(0);

//     this.x = x; 
//     this.y = y;
//     this.z = z;
//   }

//   private static class Fixture extends LXAbstractFixture 
//   {

//     private Fixture(float x, float y, float z) {
//       LXTransform t = new LXTransform();
//       t.translate(x, y, z);   
//     }
//   }
// }

public static class Strip extends LXModel {

  public static final float POINT_SPACING = 18.625f / 15.f;

  public static class Metrics {

    public final float length;
    public final int numPoints;

    public Metrics(float length, int numPoints) {
      this.length = length;
      this.numPoints = numPoints;
    }
  }

  public final Metrics metrics;
  public final float ry;

  Strip(Metrics metrics, float ry, List<LXPoint> points) {
    super(points);
    this.metrics = metrics;   
    this.ry = ry;
  }

  Strip(Metrics metrics, float ry, LXTransform transform) {
    super(new Fixture(metrics, ry, transform));
    this.metrics = metrics;
    this.ry = ry;
  }

  private static class Fixture extends LXAbstractFixture {
    private Fixture(Metrics metrics, float ry, LXTransform transform) {
      float offset = (metrics.length - (metrics.numPoints - 1) * POINT_SPACING) / 2.f;
      transform.push();
      transform.translate(offset, 1, 0);
      for (int i = 0; i < metrics.numPoints; i++) {
        LXPoint point = new LXPoint(transform.x(), transform.y(), transform.z());
        this.points.add(point);
        transform.translate(POINT_SPACING, 0, 0);
      }
      transform.pop();
    }
  }
}
class LayerDemoPattern extends LXPattern {
  
  private final BasicParameter colorSpread = new BasicParameter("Clr", 3, 0, 3);
  private final BasicParameter stars = new BasicParameter("Stars", 0, 0, 100);
  private final BasicParameter waveSpeed = new BasicParameter("Speed", 50, 0, 100);
  
  public LayerDemoPattern(LX lx) {
    super(lx);
    addParameter(colorSpread);
    addParameter(stars);
    addParameter(waveSpeed);
    addLayer(new CircleLayer(lx));
    // addLayer(new RodLayer(lx));
    for (int i = 0; i < 200; ++i) {
      addLayer(new StarLayer(lx));
    }
  }
  
  public void run(double deltaMs) {
    // The layers run automatically
  }
  
  private class CircleLayer extends LXLayer {
    
    private final SinLFO xPeriod = new SinLFO(4000, 4000 , 20000); 
    private final SinLFO brightnessX = new SinLFO(model.xMin, model.xMax, xPeriod);
  
    private CircleLayer(LX lx) {
      super(lx);
      addModulator(xPeriod).start();
      addModulator(brightnessX).start();
    }
    
    public void run(double deltaMs) {
      // The layers run automatically
      float falloff = 100 / (4*FEET);
      for (LXPoint p : model.points) {
        float yWave = sin(p.x / model.xRange * PI); 
        float distanceFromCenter = dist(p.x, p.y, model.cx, model.cy);
        float distanceFromBrightness = dist(p.x, abs(p.y - model.cy), brightnessX.getValuef(), yWave);
        colors[p.index] = LXColor.hsb(
          lx.getBaseHuef() + colorSpread.getValuef() * distanceFromCenter,
          100,
          max(0, 100 - falloff*distanceFromBrightness)
        );
      }
    }
  }
  
  private class RodLayer extends LXLayer {
    
    private final SinLFO zPeriod = new SinLFO(2000, 5000, 9000);
    private final SinLFO zPos = new SinLFO(model.zMin, model.zMax, zPeriod);
    
    private RodLayer(LX lx) {
      super(lx);
      addModulator(zPeriod).start();
      addModulator(zPos).start();
    }
    
    public void run(double deltaMs) {
      for (LXPoint p : model.points) {
        float b = 100 - dist(p.x, p.y, model.cx, model.cy) - abs(p.z - zPos.getValuef());
        if (b > 0) {
          addColor(p.index, LXColor.hsb(
            lx.getBaseHuef() + p.z,
            100,
            b
          ));
        }
      }
    }
  }
  
  private class StarLayer extends LXLayer {
    
    private final TriangleLFO maxBright = new TriangleLFO(0, stars, random(2000, 8000));
    private final SinLFO brightness = new SinLFO(-1, maxBright, random(3000, 9000)); 
    
    private int index = 0;
    
    private StarLayer(LX lx) { 
      super(lx);
      addModulator(maxBright).start();
      addModulator(brightness).start();
      pickStar();
    }
    
    private void pickStar() {
      index = (int) random(0, model.size-1);
    }
    
    public void run(double deltaMs) {
      if (brightness.getValuef() <= 0) {
        pickStar();
      } else {
        addColor(index, LXColor.hsb(lx.getBaseHuef(), 50, brightness.getValuef()));
      }
    }
  }
}



class TestHuePattern extends LXPattern 
{
  public TestHuePattern(LX lx) 
  {
    super(lx);
  }
  
  public void run(double deltaMs) 
  {
    // Access the core master hue via this method call
    float hv = lx.getBaseHuef();
    for (int i = 0; i < colors.length; ++i) 
    {
      colors[i] = lx.hsb(hv, 100, 100);
    }
  } 
}



class TestXPattern extends LXPattern 
{
  private final SinLFO xPos = new SinLFO(model.xMin, model.xMax, 4000);
  public TestXPattern(LX lx) 
  {
    super(lx);
    addModulator(xPos).trigger();
  }
  public void run(double deltaMs) 
  {
    float hv = lx.getBaseHuef();
    for (LXPoint p : model.points) 
    {
      // This is a common technique for modulating brightness.
      // You can use abs() to determine the distance between two
      // values. The further away this point is from an exact
      // point, the more we decrease its brightness
      float bv = max(0, 100 - abs(p.x - xPos.getValuef()));
      colors[p.index] = lx.hsb(hv, 100, bv);
    }
  }
}


class TestYPattern extends LXPattern 
{
  private final SinLFO yPos = new SinLFO(model.yMin, model.yMax, 4000);
  public TestYPattern(LX lx) 
  {
    super(lx);
    addModulator(yPos).trigger();
  }
  public void run(double deltaMs) 
  {
    float hv = lx.getBaseHuef();
    for (LXPoint p : model.points) 
    {
      float bv = max(0, 100 - abs(p.y - yPos.getValuef()));
      colors[p.index] = lx.hsb(hv, 100, bv);
    }
  }
}


class TestZPattern extends LXPattern 
{
  private final SinLFO zPos = new SinLFO(model.zMin, model.zMax, 4000);
  public TestZPattern(LX lx) 
  {
    super(lx);
    addModulator(zPos).trigger();
  }
  public void run(double deltaMs) 
  {
    float hv = lx.getBaseHuef();
    for (LXPoint p : model.points) 
    {
      float bv = max(0, 100 - abs(p.z - zPos.getValuef()));
      colors[p.index] = lx.hsb(hv, 100, bv);
    }
  }
}

class TestProjectionPattern extends LXPattern 
{
  private final LXProjection projection;
  private final SawLFO angle = new SawLFO(0, TWO_PI, 15000);
  
  public TestProjectionPattern(LX lx) 
  {
    super(lx);
    projection = new LXProjection(model);
    addModulator(angle).trigger();
  }
  
  public void run(double deltaMs) 
  {
    projection.reset();
    projection.center();
    projection.rotate(angle.getValuef(), 0, 0, 1);

    float hv = lx.getBaseHuef();
    for (LXVector c : projection) {
      float d = sqrt(c.x*c.x + c.y*c.y + c.z*c.z); // distance from origin
      // d = abs(d-60) + max(0, abs(c.z) - 20); // life saver / ring thing
      d = max(0, abs(c.y) - 10 + .1f*abs(c.z) + .02f*abs(c.x)); // plane / spear thing
      colors[c.index] = lx.hsb(
        (hv + .6f*abs(c.x) + abs(c.z)) % 360,
        100,
        constrain(140 - 40*d, 0, 100)
      );
    }
  } 
}
/**
 * Here's a simple extension of a camera component. This will be
 * rendered inside the camera view context. We just override the
 * onDraw method and invoke Processing drawing methods directly.
 */

class UIEngineControl extends UIWindow {
  
  final UIKnob fpsKnob;
  
  UIEngineControl(UI ui, float x, float y) {
    super(ui, "THREADS AND FPS", x, y, UIChannelControl.WIDTH, 96);
        
    y = UIWindow.TITLE_LABEL_HEIGHT;
    new UIButton(4, y, width-8, 20) {
      protected void onToggle(boolean enabled) {
        lx.engine.setThreaded(enabled);
        fpsKnob.setEnabled(enabled);
      }
    }
    .setActiveLabel("Multi-Threaded")
    .setInactiveLabel("Single-Threaded")
    .addToContainer(this);
    
    y += 24;
    fpsKnob = new UIKnob(4, y);    
    fpsKnob
    .setParameter(lx.engine.framesPerSecond)
    .setEnabled(lx.engine.isThreaded())
    .addToContainer(this);
  }
}

class UIComponentsDemo extends UIWindow {
  
  static final int NUM_KNOBS = 4; 
  final BasicParameter[] knobParameters = new BasicParameter[NUM_KNOBS];  
  
  UIComponentsDemo(UI ui, float x, float y) {
    super(ui, "UI COMPONENTS", x, y, 140, 10);
    
    for (int i = 0; i < knobParameters.length; ++i) {
      knobParameters[i] = new BasicParameter("Knb" + (i+1), i+1, 0, 4);
      knobParameters[i].addListener(new LXParameterListener() {
        public void onParameterChanged(LXParameter p) {
          println(p.getLabel() + " value:" + p.getValue());
        }
      });
    }
    
    y = UIWindow.TITLE_LABEL_HEIGHT;
    
    new UIButton(4, y, width-8, 20)
    .setLabel("Toggle Button")
    .addToContainer(this);
    y += 24;
    
    new UIButton(4, y, width-8, 20)
    .setActiveLabel("Boop!")
    .setInactiveLabel("Momentary Button")
    .setMomentary(true)
    .addToContainer(this);
    y += 24;
    
    for (int i = 0; i < 4; ++i) {
      new UIKnob(4 + i*34, y)
      .setParameter(knobParameters[i])
      .setEnabled(i % 2 == 0)
      .addToContainer(this);
    }
    y += 48;
    
    for (int i = 0; i < 4; ++i) {
      new UISlider(UISlider.Direction.VERTICAL, 4 + i*34, y, 30, 60)
      .setParameter(new BasicParameter("VSl" + i, (i+1)*.25f))
      .setEnabled(i % 2 == 1)
      .addToContainer(this);
    }
    y += 64;
    
    for (int i = 0; i < 2; ++i) {
      new UISlider(4, y, width-8, 24)
      .setParameter(new BasicParameter("HSl" + i, (i + 1) * .25f))
      .setEnabled(i % 2 == 0)
      .addToContainer(this);
      y += 28;
    }
    
    new UIToggleSet(4, y, width-8, 24)
    .setParameter(new DiscreteParameter("Ltrs", new String[] { "A", "B", "C", "D" }))
    .addToContainer(this);
    y += 28;
    
    for (int i = 0; i < 4; ++i) {
      new UIIntegerBox(4 + i*34, y, 30, 22)
      .setParameter(new DiscreteParameter("Dcrt", 10))
      .addToContainer(this);
    }
    y += 26;
    
    new UILabel(4, y, width-8, 24)
    .setLabel("This is just a label.")
    .setAlignment(CENTER, CENTER)
    .setBorderColor(ui.theme.getControlDisabledColor())
    .addToContainer(this);
    y += 28;
    
    setSize(width, y);
  }
} 
/* Build FadeCandy output */

public void buildOutputs()
{
	lx.addOutput(new FadecandyOutput(lx, "pi1.local", 7890));
	lx.addOutput(new FadecandyOutput(lx, "pi2.local", 7890));
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ContainerStrip" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
