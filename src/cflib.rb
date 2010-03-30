#!/usr/bin/ruby
require "uri"
require "net/http"
require "date"
require "pathname"
require 'etc'



class CueFinder

  def initialize(mp3Filename, start_page,radioshow_folder_path)
    @mp3Filename=mp3Filename
    @start_page=start_page
    @radioshow_folder_path=radioshow_folder_path
    p = Pathname.new(mp3Filename)
    basename = "#{p.basename(".mp3")}"
    @cue_file_name="#{basename}.cue"

    path_to_config=Etc.getpwuid.dir+"/.cuefinder"
    if(!File.exist?(path_to_config))
      raise "Please provide proxy settings in ~/.cuefinder"
    end
    props = load_properties(path_to_config)
    @proxy_host =props["proxy_host"];
    @proxy_port =props["proxy_port"];
    puts "using proxy: #{@proxy_host}:#{@proxy_port}"

  end

  def load_properties(properties_filename)
    properties = {}
    File.open(properties_filename, 'r') do |properties_file|
      properties_file.read.each_line do |line|
        line.strip!
        if (line[0] != ?# and line[0] != ?=)
          i = line.index('=')
          if (i)
            properties[line[0..i - 1].strip] = line[i + 1..-1].strip
          else
            properties[line] = ''
          end
        end
      end
    end
    properties
  end

  def get(url)
    puts "downloading url: #{url}"
    browser=Net::HTTP::Proxy(@proxy_host , @proxy_port).start(@start_page)
    res=browser.get(url, {"Referer" => "http://cuenation.com/?page=cues&folder=gdjb"})
    return res.body
  end

  def download_cue()
    release_no=parse_release_no()
    if(release_no!=nil) then
      html=get(@radioshow_folder_path)
      cue_url=parse_url_to_cue_file(html,release_no)
      puts "cueUrl is #{cue_url}, downloading"
      content=get(cue_url)
      f=File.new(@cue_file_name,"w")
      f.write(content)
      f.close
      puts "cue file saved to #{@cue_file_name}"
      #      puts "#{content}#"
      return @cue_file_name
    else
      puts "release no could not be parsed, sorry"
      return nil
    end
  end

  def call_mp3splt(cue_file_name,mp3_file_name)
    p=Pathname.new( mp3_file_name)
    cmd = "mp3splt -c \"#{cue_file_name}\" -d \"#{p.dirname}\" \"#{mp3_file_name}\""
    puts cmd
    ret=system(cmd)
    if (!ret) then
      puts "could not split mp3 file: #{mp3_file_name}"
    else
      File.delete(mp3_file_name)
    end
  end

  def process
    cue_file=download_cue()
    if cue_file!=nil then
      call_mp3splt(@cue_file_name,@mp3Filename)
    else
      puts "could not find cue sheet for: #{@mp3Filename}"
    end
  end

end

class ASOTParser < CueFinder
  
  def initialize(mp3Filename)
    super(mp3Filename,"cuenation.com/","?page=cues&folder=asot")
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Episode ([0-9]+)\.mp3/
    puts "ASOT release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo)
    urls=text.scan /(download.php[?]type=cue.*_#{asotNo}_.*\.cue)\"\>\<img/
    puts "First found url to cue file: #{urls[0]}"
    url=urls[0][0].gsub("&amp;", "&")
    return url
  end

end



class MarkusShultzParser < CueFinder
  def initialize(mp3Filename)
    super(mp3Filename,"cuenation.com/","?page=cues&folder=gdjb")
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Global DJ Broadcast \((.*)\).*\.mp3/

    if(release_no[0] == nil) then 
      #another pattern
      puts "trying 2nd pattern"
      release_no=@mp3Filename.scan /\((.*)\)\.mp3/
      #extract from array
      release_no=release_no[0][0]
      #25 March 2010
      d,m,y=release_no.split(" ")
      #March -> "03"
      m_number=Date::MONTHNAMES.index(m).to_s.rjust(2,"0")
      release_no="#{d}-#{m_number}-#{y}"
    end

    puts "Global DJ Broadcast release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo)
    puts "cue_url_release_pattern=#{asotNo}"
    urls=text.scan /(download.php[?]type=cue.*-#{asotNo}-.*\.cue)\"\>\<img/
    puts "scanned urls array: #{urls}"
    url=urls[0][0].gsub("&amp;", "&")
    puts "scanned url: #{url}"
    return url
  end

end

class MagicIslandParser < CueFinder

  def initialize(mp3Filename)
    super(mp3Filename,"cuenation.com/","?page=cues&folder=magicisland")
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Episode_([0-9]+)-/
    release_no=release_no[0][0]
    puts "Magic Island release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo)
    urls=text.scan /(download.php[?]type=cue.*music_for_balearic_people_#{asotNo}_.*\.cue)\"\>\<img/

    puts "First found url to cue file: #{urls[0]}"
    url=urls[0][0].gsub("&amp;", "&")
    return url
  end

end


class CueFinderFactory

  def self.make_cuefinder(file_name)
    if(file_name.index("Markus")!=nil) then
      return MarkusShultzParser.new(file_name )
    end

    if(file_name.index("Armin")!=nil) then
      return ASOTParser.new(file_name )
    end

    if(file_name.index("Balearic")!=nil) then
      return MagicIslandParser.new(file_name )
    end

    return nil

  end
end



