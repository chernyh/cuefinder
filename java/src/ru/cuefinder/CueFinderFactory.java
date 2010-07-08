package ru.cuefinder;

/**
 * Created by IntelliJ IDEA.
 * User: chernyh
 * Date: 05.06.2010
 * Time: 23:05:09
 * To change this template use File | Settings | File Templates.
 */
public class CueFinderFactory
{
    public static CueFinder makeCueFinder( String file_name )
    {
        if( file_name.contains( "Markus" ) )
        {
            return new MarkusShultzParser( file_name );
        }
        if( file_name.contains( "Armin" ) )
        {
            return new ASOTParser( file_name );
        }
        if( file_name.contains( "Shah" ) )
        {
            return new MagicIslandParser( file_name );
        }
        if( file_name.contains( "Club Life" ) )
        {
            return new TiestoParser( file_name );
        }
        if( file_name.contains( "Vonyc" ) || file_name.contains( "vonyc" ) )
        {
            return new VonycParser( file_name );
        }

        return null;
    }

}
