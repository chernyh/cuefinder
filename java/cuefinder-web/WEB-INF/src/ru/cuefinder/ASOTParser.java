package ru.cuefinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: chernyh
 * Date: 05.06.2010
 * Time: 2:41:46
 * To change this template use File | Settings | File Templates.
 */
public class ASOTParser extends CueFinder
{
    public ASOTParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=asot";
    }

    @Override
    protected String parse_url_to_cue_file( String html, String asotNo, int part_no )
    {
        Pattern p = Pattern.compile( "(download.php[?]type=cue.*" + asotNo + ".*\\.cue)\"><img" );
//        urls=text.scan /"(download.php[?]type=cue.*#{asotNo}.*\.cue)\"\>\<img/"
        Matcher m = p.matcher( html );
        if( m.matches() )
        {
            return m.group();
        } else return null;

    }


    public String parse_release_no() throws Exception
    {
        release_no  = getFirstMatch( "Episode ([0-9]+)\\.mp3", mp3Filename );
        if( release_no  != null )
        {
            return release_no ;
        }
        release_no  = getFirstMatch( "Trance[_ ](([0-9]+))[_ -]", mp3Filename );
        System.out.println( "ASOT release is '" + release_no + "'" );
        return release_no ;

    }


}
