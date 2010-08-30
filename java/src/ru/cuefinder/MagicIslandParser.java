package ru.cuefinder;

public class MagicIslandParser extends CueFinder
{
    public MagicIslandParser( String mp3Filename )
    {
        super( mp3Filename );
    }

    @Override
    protected String get_radioshow_folder_path()
    {
        return "?page=cues&folder=magicisland";
    }


    public String parse_release_no() 
    {
        String match = getFirstMatch( "Episode ([0-9]+)", mp3Filename );
        if( match != null )
        {
            return match;
        }
        log.add( "trying 2nd pattern" );

        match = getFirstMatch( "[_ ]+([0-9]{3})[ _-]+", mp3Filename );
        log.add( "Magic Island release is '" + match + "'" );
        return match;

    }

    @Override
    protected String parse_url_to_cue_file( String html, String asotNo, int part_no )
    {
        release_no = getFirstMatch( "(download.php[?]type=cue.*magicisland.*" + asotNo + ".*\\.cue)\\\"\\>\\<img", html );
        if( release_no!= null )
        {
            return release_no;
        }
        log.add( "trying 2nd pattern" );
        release_no = getFirstMatch( "(download.php[?]type=cue.*People_" + asotNo + "[_-].*\\.cue)\\\"\\>\\<img", html );
        return release_no;
    }


}