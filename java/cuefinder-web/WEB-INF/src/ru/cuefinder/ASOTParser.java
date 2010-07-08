package ru.cuefinder;

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
        return getFirstMatch( "(download.php[?]type=cue.*" + asotNo + ".*\\.cue)\\\"\\>\\<img", html );
    }


    public String parse_release_no() 
    {
        release_no = getFirstMatch( "Episode ([0-9]+).*\\.mp3", mp3Filename );
        if( release_no != null )
        {
            return release_no;
        }
        release_no = getFirstMatch( "Trance[_ ](([0-9]+))[_ -]", mp3Filename );
        log.add( "ASOT release is '" + release_no + "'" );
        return release_no;

    }


}
