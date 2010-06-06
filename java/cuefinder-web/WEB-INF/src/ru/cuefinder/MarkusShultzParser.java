package ru.cuefinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkusShultzParser extends CueFinder
{
    public MarkusShultzParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=gdjb";
    }

    @Override
    protected String parse_url_to_cue_file( String html, String asotNo, int part_no )
    {
        return getFirstMatch( "(download.php[?]type=cue.*-" + asotNo + "-.*\\.cue)\\\"\\>\\<img", html );
    }


    public String parse_release_no() throws Exception
    {
        release_no = getFirstMatch( "Global DJ Broadcast \\((.*?)\\).*\\.mp3", mp3Filename );
        if( release_no != null )
        {
            return release_no;
        }
        System.out.println( "trying 2nd pattern" );

        release_no = getFirstMatch( "\\((.*)\\)\\.mp3", mp3Filename );
        if( release_no == null )
        {
            System.out.println( "trying 3rd pattern" );
            release_no = getFirstMatch( "Tour_(.*_.*_.*)\\.mp3", mp3Filename );
        }

        release_no = release_no.replaceAll( "[_-]", " " );


        Date d;
        SimpleDateFormat df = new SimpleDateFormat( "dd MM yyyy" );
        try
        {

            d = df.parse( release_no );
        }
        catch( ParseException e )
        {
            try
            {
                SimpleDateFormat df2 = new SimpleDateFormat( "yyyy mm dd" );
                d = df2.parse( release_no );
            }
            catch( ParseException e2 )
            {
                SimpleDateFormat df3 = new SimpleDateFormat( "dd MMMM yyyy", Locale.US );

                d = df3.parse( release_no );

            }
        }


        release_no = new SimpleDateFormat( "dd-MM-yyyy" ).format( d );
        return release_no;

    }


}