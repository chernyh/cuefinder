package ru.cuefinder;

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

        if( file_name.contains( "Corsten" ) )
        {
            return new CorstenParser( file_name );
        }
        if( file_name.contains( "Future Sound Of Egypt" ) )
        {
            return new AlyAndFilaParser( file_name );
        }

        return null;
    }

}
