--KEYS[1] = rate limiter key/redis key
--ARGV[1] = current timeStamp (ms)
--ARGV[2] = window size (ms)
--ARGV[3] = max allowd request

local key=KEYS[1]
local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
--start time of window
local window_start=now-window
--remove expired req
redis.call('ZREMRANGEBYSCORE',key,0,window_start)
--count 
local current_count = redis.call('ZCARD',key)
--allow
if current_count<limit then
	redis.call('ZADD',key,now,tostring(now))
	redis.call('PEXPIRE',key,window)
	return {1,limit-current_count-1}
	end
--block
return{0,0}