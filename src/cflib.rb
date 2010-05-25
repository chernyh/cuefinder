#!/usr/bin/ruby
require "uri"
require "net/http"
require "date"
require "pathname"
require 'etc'



class CueFinder

  def initialize(mp3Filename)
    @mp3Filename=mp3Filename
    @release_no=parse_release_no()
    @part_no=parse_part_no()
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
    @proxy_set=props["proxy_set"]; 
    puts "using proxy: #{@proxy_host}:#{@proxy_port} , set: #{@proxy_set}"

  end
  
  def parse_part_no()
    return nil
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
    if(@proxy_set == nil || @proxy_set !="off"  )
      browser=Net::HTTP::Proxy(@proxy_host , @proxy_port).start("cuenation.com/")
    else
       puts "working without proxy"
       browser=Net::HTTP.start("cuenation.com/")
    end

    res=browser.get(url, {"Referer" => "http://cuenation.com/?page=cues&folder=gdjb"})
    return res.body
  end

  def find_url_to_cue_file(html)
    cue_url=parse_url_to_cue_file(html,@release_no,@part_no)
    raise "Sorry , can't find url for release #{@release_no}" if cue_url[0].nil?
    cue_url=cue_url[0][0]
    #urls are returned with &amp; , like a=1&amp;b=2&amp;c=3
    cue_url=cue_url.gsub("&amp;", "&")
    return cue_url
  end

  def download_cue()
    if(@release_no!=nil) then
      html=get(get_radioshow_folder_path())
      cue_url=find_url_to_cue_file(html)
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

  #when radioshow is plit into 2 parts,we need to distinguish them by prefix
  #i.e.:
  # 101 Tiesto - Track1.mp3
  # 102 Tiesto - Track2.mp3
  #... second part begins:
  # 201 Tiesto - Track1.mp3
  # 202 Tiesto - Track2.mp3
  def make_output_format()
    output_format="@n+@p+@t"
    if(@part_no!=nil)
      output_format="#{@part_no}"+output_format
    end
    return output_format
  end

  def call_mp3splt(cue_file_name,mp3_file_name)
    p=Pathname.new( mp3_file_name)
    output_format=make_output_format()
    cmd = "mp3splt -o #{output_format} -c \"#{cue_file_name}\" -d \"#{p.dirname}\" \"#{mp3_file_name}\""
    puts cmd
    ret=system(cmd)
    if (!ret) then
      puts "could not split mp3 file: #{mp3_file_name}"
    else
        if(@part_no==nil) then
            File.delete(mp3_file_name)
        else
            #puts "mp3 file not deleted (radioshows containing more than 1 part not yet well tested)"
            File.delete(mp3_file_name)
        end
      File.delete(cue_file_name)
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
  
  def get_radioshow_folder_path
    return "?page=cues&folder=asot"
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Episode ([0-9]+)\.mp3/
    if(release_no[0] == nil) then 
      #another pattern
      puts "trying 2nd pattern"
      release_no=@mp3Filename.scan /Trance[_ ](([0-9]+))[_ -]/
    end
    release_no=release_no[0][0]
    
    puts "ASOT release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo,part_no)
    urls=text.scan /(download.php[?]type=cue.*#{asotNo}.*\.cue)\"\>\<img/
    return urls
  end

end



class MarkusShultzParser < CueFinder

  def get_radioshow_folder_path
    return "?page=cues&folder=gdjb"
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Global DJ Broadcast \((.*)\).*\.mp3/

    if(release_no[0] == nil) then 
      #another pattern
      puts "trying 2nd pattern"
      release_no=@mp3Filename.scan /\((.*)\)\.mp3/
    end
    if(release_no[0] == nil) then 
      #another pattern
      puts "trying 3rd pattern"
      release_no=@mp3Filename.scan /Tour_(.*_.*_.*)\.mp3/
    end

    #extract from array
    release_no=release_no[0][0]
    release_no=release_no.gsub("_", " ")
    #25 March 2010
    d,m,y=release_no.split(" ")
    #March -> "03"
    m_number=Date::MONTHNAMES.index(m).to_s.rjust(2,"0")
    d=d.rjust(2,"0")
    release_no="#{d}-#{m_number}-#{y}"


    puts "Global DJ Broadcast release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo,part_no)
    return text.scan /(download.php[?]type=cue.*-#{asotNo}-.*\.cue)\"\>\<img/
  end

end

class MagicIslandParser < CueFinder

  def get_radioshow_folder_path
    return "?page=cues&folder=magicisland"
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Episode ([0-9]+)/
    if(release_no[0] == nil) then 
      #another pattern
      puts "trying 2nd pattern"
      release_no=@mp3Filename.scan /_([0-9]+)-/
    end
    release_no=release_no[0][0]
    puts "Magic Island release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo,part_no)
    urls=text.scan /(download.php[?]type=cue.*music_for_balearic_people_#{asotNo}[_-].*\.cue)\"\>\<img/
    if(urls[0] == nil )
      puts "Trying 2nd regex"
      urls=text.scan /(download.php[?]type=cue.*People_#{asotNo}[_-].*\.cue)\"\>\<img/
    end
    return urls
  end

end

class TiestoParser < CueFinder

  def get_radioshow_folder_path
    return "?page=cues&folder=clublife"
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /Club Life ([0-9]+) /
    release_no=release_no[0][0]
    puts "Club Life  release is '#{release_no}'"
    return release_no
  end
  def parse_part_no()
    part_no=@mp3Filename.scan /part ([0-9]+)/
    part_no=part_no[0][0]
    puts "Club Life  part_no is '#{part_no}'"
    return part_no
  end

  def parse_url_to_cue_file(text,asotNo,part_no)
    return text.scan /(download.php[?]type=cue.*filename=0#{part_no}-tiesto_-_club_life_#{asotNo}-.*\.cue)\"\>\<img/
  end

end

class VonycParser < CueFinder

  def get_radioshow_folder_path
    return "?page=cues&folder=vonyc"
  end

  def parse_release_no()
    release_no=@mp3Filename.scan /sessions_([0-9]+)-/
    release_no=release_no[0][0]
    puts "Vonyc Sessions release is '#{release_no}'"
    return release_no
  end

  def parse_url_to_cue_file(text,asotNo,part_no)
    return text.scan /(download.php[?]type=cue.*vonyc_sessions_#{@release_no}-.*\.cue)\"\>\<img/
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

    if(file_name.index("Shah")!=nil) then
      return MagicIslandParser.new(file_name )
    end

    if(file_name.index("Club Life")!=nil) then
      return TiestoParser.new(file_name )
    end

    if(file_name.index("Vonyc")!=nil || file_name.index("vonyc")!=nil ) then
      return VonycParser.new( file_name )
    end

    return nil

  end
end



