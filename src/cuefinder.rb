#!/usr/bin/ruby
require "uri"
require "net/http"


PROXY_ADDR = ''
PROXY_PORT = 3128

class ASOTParser
  
  def initialize(mp3Filename)
    @mp3Filename=mp3Filename
    @start_page="cuenation.com/"
    @asot_folder_path="?page=cues&folder=asot"
    @cue_file_name="cue.cue"

  end

  def get(url)
    #  res=HTTP_BROWSER.get(url,HEADERS)
    browser=Net::HTTP::Proxy(PROXY_ADDR, PROXY_PORT).start(@start_page)
    res=browser.get(url)
    return res.body
  end
  
  def parse_release_no()
    release_no=@mp3Filename.scan /Episode ([0-9]+)\.mp3/
    puts "ASOT release is '#{release_no}'"
    return release_no
  end
  
  def parse_url_to_cue_file(text,asotNo)
    urls=text.scan /(download.php[?]type=cue.*_#{asotNo}_.*\.cue)\"\>\<img/ 
    #    puts "First found url to cue file: #{urls[0]}####"
    url=urls[0][0].gsub("&amp;", "&")
    return url
  end

  def download_cue()
    release_no=parse_release_no()
    if(release_no!=nil) then
      html=get(@asot_folder_path)
      cue_url=parse_url_to_cue_file(html,release_no)
      puts "cueUrl is #{cue_url}, downloading"
      content=get(cue_url)
      f=File.new(@cue_file_name,"w")
      f.write(content)
      puts "cue file saved to "
      return @cue_file_name
    else
      puts "release no could not be parsed, sorry"
      return nil
    end
  end

end


fileName=ARGV[0]
#file_name="Armin van Buuren presents - A State of Trance Episode 430.mp3#"

if(file_name != nil) then
  ap=ASOTParser.new(file_name)
  cue_file=ap.download_cue()
  if cue_file!=nil then
    ret=system("mp3splt -c #{cue_file} #{file_name}")
    if (!ret) then
      puts "could not split mp3 file"
    end
  else
    puts "could not find cue sheet for: #{file_name}"
  end
else
  puts "Please provide file name"
end

