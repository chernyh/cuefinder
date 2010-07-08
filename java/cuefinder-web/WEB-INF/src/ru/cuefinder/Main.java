package ru.cuefinder;

import java.io.File;
import java.io.FileFilter;

public class Main
{
    public static void main( String[] args ) throws Exception
    {
        if( args.length == 0 )
        {
            System.out.println( "Please provide file name" );
            return;
        }
        String fName = args[ 0 ];
        File f = new File( fName );
        if( f.isDirectory() )
        {
            System.out.println( "Recursive mode." );
            traverse( fName );
        }
        else
        {
            CueFinder cf = CueFinderFactory.makeCueFinder( fName );
            if( cf == null )
            {
                System.out.println( "Cannot detect what radioshow it is:" + fName );
                return;
            }
            processOneFile( cf );
        }
    }

    private static class _50MBFilter implements FileFilter
    {
        public boolean accept( File file )
        {
            return file.isDirectory() || ( file.length() > 50000000 );//50MB minimum
        }
    }

    public static void traverse( String dirName ) throws Exception
    {
        File dir = new File( dirName );
        for( File f : dir.listFiles( new _50MBFilter() ) )
        {
            String path = f.getAbsolutePath();
            if( f.isDirectory() )
            {
//                System.out.println( "Diving into " + path );
                traverse( path );
            }
            else
            {
                CueFinder cf = CueFinderFactory.makeCueFinder( path );
                if( cf != null )
                {
                    System.out.println( "detected radioshow :" + path );
                    try
                    {
                        processOneFile( cf );
                    }
                    catch( Exception e )
                    {
                        System.out.println( "Problem processing " + path + " : " + e.getMessage() );
                    }
                }
            }
        }
    }

    public static void processOneFile( CueFinder cf )
    {
        try
        {
            cf.process();
        }
        catch( Exception e )
        {
            System.out.println( "Problem processing " + cf.getMp3Filename() + " : " + e.getMessage() );
        }
        System.out.println( cf.getOutput() );

    }
}
