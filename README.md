## decimal4j
Java library for fast fixed precision arithmetic based on longs with support for up to 18 decimal places.

#### Javadoc API
http://decimal4j.org/javadoc

#### Features
 - Fixed precision arithmetic with 0 to 18 decimal places
   - Implementation based on unscaled long value
   - Option to throw an exception when an arithmetic overflow occurs
 - Scale
   - Type of a variable defines the scale (except for wildcard types)
   - Result has usually the same scale as the primary operand (also for multiplication and division)
 - Type Conversion  
   - Efficient conversion from and to various other number types
   - Convenience methods to directly inter-operate with other data types (long, double, ...)
   - All rounding modes supported (default: HALF_UP)
 - Efficiency
   - Fast and efficient implementation
   - ``MutableDecimal`` implementation for chained operations
   - Primitive arithmetic API for zero-garbage computations (with unscaled long values)

#### Quick Start

###### Example 1: Interest Calculation

    public class Interest {
	    public static void main(String[] args) {
		    Decimal2f principal = Decimal2f.valueOf(9500);
		    Decimal3f rate = Decimal3f.valueOf(0.067);
		    Decimal2f time = Decimal2f.valueOf(1).divide(4);
		    Decimal2f interest = principal.multiplyBy(rate.multiplyExact(time));
		    System.out.println("First quarter interest:  $" + interest);
	    }
    }
    
will output

    First quarter interest: $159.13

###### Example 2: Circumference of a Circle

    public class Circle {
	    public static void main(String[] args) {
		    Decimal18f PI = Decimal18f.valueOf(Math.PI);
		    Decimal2f radius = Decimal2f.valueOf(5);
		    Decimal2f circ = radius.multiplyBy(PI.multiply(2));
		    System.out.println("Circumference of circle with 5m radius is ~" + circ + "m");
		    System.out.println("Circumference of circle with 5m radius is ~" + (2*Math.PI * 5) + "m");

		    Decimal2f down = radius.multiplyBy(PI.multiply(2), RoundingMode.DOWN);
		    System.out.println("Circumference of circle with 5m radius is larger than " + down + "m");
	    }
    }

will output

    Circumference of circle with 5m radius is ~31.42m
    Circumference of circle with 5m radius is ~31.41592653589793m
    Circumference of circle with 5m radius is larger than 31.41m

#### Maven
decimal4j has not been released yet. The current snapshot version can be referenced as follows:

    <dependency>
      <groupId>org.decimal4j</groupId>
      <artifactId>decimal4j</artifactId>
      <version>1.0.1-SNAPSHOT</version>
      <scope>compile</scope>
    </dependency>

You have to specify the sonatype snapshot repository to access snapshot versions:

    <repositories>
  	  <repository>
  		  <id>sonatype.snapshots</id>
    	  <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    	  <releases><enabled>false</enabled></releases>
    	  <snapshots><enabled>true</enabled></snapshots>
    	</repository>
    </repositories>

#### Download
decimal4j has not been released yet. The current snapshot version can be downloaded from the sonatype snapshot repository:

https://oss.sonatype.org/content/repositories/snapshots/org/decimal4j

