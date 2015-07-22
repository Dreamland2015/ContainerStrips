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
    private static final int NUMBER_OF_LEDS = 85;
    
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