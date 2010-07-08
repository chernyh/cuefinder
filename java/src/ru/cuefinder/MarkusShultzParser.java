package ru.cuefinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        String url = getFirstMatch( "(download.php[?]type=cue.*-" + asotNo + "-.*\\.cue)\\\"\\>\\<img", html );
        if( url == null )
        {
            String[] datePart = asotNo.split( "-" );
            url = getFirstMatch( "(download.php[?]type=cue.*-" + datePart[ 2 ] + "-" + datePart[ 1 ] + "-" + datePart[ 0 ] + "-.*\\.cue)\\\"\\>\\<img", html );
        }
        return url;
    }


    public String parse_release_no()
    {
        release_no = getFirstMatch( "Global DJ Broadcast \\((.*?)\\).*\\.mp3", mp3Filename );
        if( release_no == null )
        {
            log.add( "trying 2nd pattern" );

            release_no = getFirstMatch( "\\((.*)\\)\\.mp3", mp3Filename );
            if( release_no == null )
            {
                log.add( "trying 3rd pattern" );
                release_no = getFirstMatch( "Tour_(.*_.*_.*)\\.mp3", mp3Filename );
            }
        }
        if( release_no == null )
        {
            return null;
        }

        release_no = release_no.replaceAll( "[_-]", " " );


        Date d = null;
        SimpleDateFormat df = new SimpleDateFormat( "dd MM yyyy" );
        try
        {
            d = df.parse( release_no );
            if( d.before( new Date( 2000, 1, 1 ) ) || d.before( new Date( 2020, 1, 1 ) ) )
            {
                throw new ParseException( "bad date", -1 );
            }
        }
        catch( ParseException e )
        {
            try
            {
                SimpleDateFormat df2 = new SimpleDateFormat( "yyyy MM dd" );
                d = df2.parse( release_no );
            }
            catch( ParseException e2 )
            {
                SimpleDateFormat df3 = new SimpleDateFormat( "dd MMMM yyyy", Locale.US );

                try
                {
                    d = df3.parse( release_no );
                }
                catch( ParseException e3 )
                {
                    e3.printStackTrace();
                }

            }
        }

        release_no = new SimpleDateFormat( "dd-MM-yyyy" ).format( d );
        return release_no;

    }
}