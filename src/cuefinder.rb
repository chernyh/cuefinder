#!/usr/bin/ruby
require "uri"
require "net/http"


PROXY_ADDR = ''
PROXY_PORT = 3128

class CueFinder

  def initialize(mp3Filename, start_page,radioshow_folder_path)
    @mp3Filename=mp3Filename
    @start_page=start_page
    @radioshow_folder_path=radioshow_folder_path
    @cue_file_name="cue.cue"

  end

  def get(url)
    puts "downloading url: #{url}"
    browser=Net::HTTP::Proxy(PROXY_ADDR, PROXY_PORT).start(@start_page)
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
      puts "cue file saved to #{@cue_file_name}"
      puts "#{content}"
      return @cue_file_name
    else
      puts "release no could not be parsed, sorry"
      return nil
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
    puts "Global DJ Broadcast release is '#{release_no}'"
    return release_no[0][0]
  end

  def parse_url_to_cue_file(text,asotNo)
    year,month,date = asotNo.scan(/\d+/)
    cue_url_release_pattern="#{date}-#{month}-#{year}"
    puts "cue_url_release_pattern=#{cue_url_release_pattern}"
    urls=text.scan /(download.php[?]type=cue.*_#{cue_url_release_pattern}.*\.cue)\"\>\<img/
    puts "scanned urls array: #{urls}"
    url=urls[0][0].gsub("&amp;", "&")
    puts "scanned url: #{url}"
    return url
  end

end


file_name=ARGV[0]
#file_name="Armin van Buuren presents - A State of Trance Episode 430.mp3##"
file_name="Markus Schulz - Global DJ Broadcast (2010-03-11) [PS].mp3"

if(file_name != nil) then
#  ap=ASOTParser.new(file_name )
  ap=MarkusShultzParser.new(file_name )
  cue_file=ap.download_cue()
  if cue_file!=nil then
    ret=system("mp3splt -c #{cue_file} #{file_name }")
    if (!ret) then
      puts "could not split mp3 file"
    end
  else
    puts "could not find cue sheet for: #{file_name }"
  end
else
  puts "Please provide file name"
end

