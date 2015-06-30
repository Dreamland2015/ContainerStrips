static class Model extends LXModel 
{
  
  public Model() 
  {
    super(new Fixture());
  }
  
  private static class Fixture extends LXAbstractFixture 
  {
    
    private static final int z = 0;
    private static final int OFFSET = FEET;
    private static final int NUMBER_OF_LEDS = 50;
    
    private Fixture() 
    {
      // Here's the core loop where we generate the positions
      // of the points in our model
      // for (int ledPoint = 0; ledPoint < 8; ++ledPoint) 
      for (int n = 0; n < NUMBER_OF_LEDS; ++n)
      {
        addPoint(new LXPoint(n*FEET,0));
      }
    }
  }
}

