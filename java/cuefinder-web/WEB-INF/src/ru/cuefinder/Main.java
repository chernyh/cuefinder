package ru.cuefinder;

/**
 * Created by IntelliJ IDEA.
 * User: chernyh
 * Date: 05.06.2010
 * Time: 23:24:34
 * To change this template use File | Settings | File Templates.
 */
public class Main
{
    public static void main( String[] args ) throws Exception
    {
        if( args.length == 0 )
        {
            System.out.println( "Please provide file name" );
            return;
        }

        String fName = args[0];
        CueFinder cf = CueFinderFactory.makeCueFinder( fName );
        if( cf == null )
        {
            System.out.println( "Cannot detect what radioshow it is:" + fName );
            return;
        }
        cf.process();
    }
}
