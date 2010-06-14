package ru.cuefinder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CueFinder
{
    private static final String CUENATION_URL_PREFIX = "http://cuenation.com/";
    String mp3Filename;
    String release_no;
    int part_no;
    String cue_file_name;

    List<String> log = new ArrayList<String>();

    public CueFinder( String mp3Filename )
    {
        this.mp3Filename = mp3Filename;
        try
        {
            this.release_no = parse_release_no();
        } catch( Exception e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.part_no = parse_part_no();

        File mp3File = new File( mp3Filename );
        File parent = mp3File.getParentFile();
        this.cue_file_name = ( parent == null ? "" : parent.getAbsolutePath() + "/" ) + mp3File.getName() + ".cue";

//    path_to_config=Etc.getpwuid.dir+"/.cuefinder"
//    if(!File.exist?(path_to_config))
//      raise "Please provide proxy settings in ~/.cuefinder"
//    end
//    props = load_properties(path_to_config)
//    @proxy_host =props["proxy_host"];
//    @proxy_port =props["proxy_port"];
//    @proxy_set=props["proxy_set"];
//    puts "using proxy: #{@proxy_host}:#{@proxy_port} , set: #{@proxy_set}"

    }

    public abstract String parse_release_no() throws Exception;

    protected int parse_part_no()
    {
        return 0;
    }


    protected byte[] get( String url ) throws IOException
    {
        log.add( "downloading url: " + url );
        HttpClient hc = new DefaultHttpClient();
        HttpGet get = new HttpGet( url );
        get.addHeader( "Referer", "http://cuenation.com/?page=cues&folder=gdjb" );
        HttpResponse resp = hc.execute( get );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpEntity entity = resp.getEntity();
        entity.writeTo( baos );

        return baos.toByteArray();

    }

    public String find_url_to_cue_file( String html ) throws Exception
    {
        String cue_url = parse_url_to_cue_file( html, release_no, part_no );
        if( cue_url == null )
        {
            String message = "Sorry , can't find url for release " + release_no;
            System.out.println( message );
            System.out.println( html );
            throw new Exception( message );
        }

        return cue_url;
    }

    protected abstract String parse_url_to_cue_file( String html, String release_no, int part_no );

    private String decode( byte[] bytes ) throws Exception
    {
        if( getForcedCueCharset() == null )
        {
            return new String( bytes );
        }
        Charset charset = Charset.forName( getForcedCueCharset() );
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer cbuf = decoder.decode( ByteBuffer.wrap( bytes ) );
        return cbuf.toString();
    }

    protected String download_cue() throws Exception
    {
        if( release_no != null )
        {
            String html = new String( get( CUENATION_URL_PREFIX + get_radioshow_folder_path() ) );
            String cue_url = CUENATION_URL_PREFIX + find_url_to_cue_file( html );
            log.add( "cueUrl is " + cue_url + ", downloading" );
            byte[] content = get( cue_url );
            File f = new File( cue_file_name );

            String decoded = decode( content );
            System.out.println( "Cue file:\n" + decoded );

            Writer out = new OutputStreamWriter( new FileOutputStream( f ), "UTF-8" );

            out.write( decoded );
            out.close();
            log.add( "cue file saved to " + cue_file_name );
            return cue_file_name;
        } else
        {
            log.add( "release no could not be parsed, sorry" );
            return null;
        }
    }

    protected String getForcedCueCharset()
    {
        return null;
    }

    protected abstract String get_radioshow_folder_path();

    /**
     * #when radioshow is plit into 2 parts,we need to distinguish them by prefix
     * #i.e.:
     * # 101 Tiesto - Track1.mp3
     * # 102 Tiesto - Track2.mp3
     * #... second part begins:
     * # 201 Tiesto - Track1.mp3
     * # 202 Tiesto - Track2.mp3
     */
    String make_output_format()
    {
        String output_format = "@n+@p+@t";
        if( part_no != 0 )
            output_format = part_no + output_format;

        return output_format;
    }

    int executeCommand( File dir, String... args ) throws IOException, InterruptedException
    {

        Process proc = Runtime.getRuntime().exec( args, null, dir );

        BufferedReader in = new BufferedReader(
                new InputStreamReader( proc.getInputStream() ) );
        String line = null;
        while( ( line = in.readLine() ) != null )
        {
            System.out.println( line );
        }

        BufferedReader ein = new BufferedReader(
                new InputStreamReader( proc.getErrorStream() ) );
        line = null;
        while( ( line = ein.readLine() ) != null )
        {
            System.out.println( line );
        }

        proc.waitFor();
        return proc.exitValue();
    }

    void call_mp3splt( String cue_file_name, String mp3_file_name ) throws IOException, InterruptedException
    {
        File mp3File = new File( mp3_file_name );
        File cueFile = new File( cue_file_name );
        String output_format = make_output_format();
        String cmd = "mp3splt -o " + output_format + " -c \"" + cue_file_name + "\"  \"" + mp3_file_name + "\"";
        log.add( cmd );

        if( executeCommand( mp3File.getParentFile(),
                "mp3splt",
                "-Q",// hangs up without it
                "-o",
                output_format,
                "-c",
                cueFile.getName(),
                mp3File.getName()
        ) != 0 )
        {
            log.add( "could not split mp3 file: " + mp3_file_name );
        } else
        {
            if( part_no == 0 )
            {
//                mp3File.delete();
            }
            //else
//            log.add("mp3 file not deleted (radioshows containing more than 1 part not yet well tested)");
//                mp3File.delete();

//                new File( cue_file_name ).delete();
        }
    }

    public void process() throws Exception, InterruptedException
    {
        String cue_file = download_cue();
        if( cue_file != null )
        {
            call_mp3splt( cue_file_name, mp3Filename );
        } else
        {
            log.add( "could not find cue sheet for: " + mp3Filename );
        }
    }

    String getFirstMatch( String regex, String input )
    {
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( input );
        if( m.find() )
        {
            return m.group( 1 ).replaceAll( "&amp;", "&" );
        } else
            return null;

    }

    public String getMp3Filename()
    {
        return mp3Filename;
    }

    public String getOutput()
    {
        return log.toString();
    }

}
