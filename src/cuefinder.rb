#!/usr/bin/ruby

#add local path to search path for require statement, so it can fins cflib.rb
puts "including #{File.dirname( __FILE__)}"
$: << File.dirname( __FILE__)

require "uri"
require "net/http"
require "cflib"

file_name=ARGV[0]

if(file_name == nil) then
  puts "Please provide file name"
  exit
end

cf=CueFinderFactory.make_cuefinder(file_name)
if(cf==nil) then
  puts "Cannot detect what radioshow it is:#{file_name}"
  exit
end
cf.process()

