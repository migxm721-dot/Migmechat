"""
helper methods for generating a hash for 
challenge response authentication.
"""
__author__ = "Ali-Akber Saifee"
__copyright__ = "Copyright 2011, ProjectGoth"
__email__ = "ali@mig33global.com"


def to_int32 (x):
    min = -2147483648# -2^31
    max = 2147483647#  2^31 - 1
    d = 4294967296 #2^32
    
    while (x > max):
        x -= d
    while (x < min):
        x += d
    return x

def to_ascii(cha):
    val = ord((cha + '')[0])
    if (val < 32 or val > 126):
        return 0
    return val

def mig33hash(key, pwd):
    """
    returns a hash required for challenge-response
    authentication for the php site.    
    """
    hash = 0 
    n = len(key+pwd)
    for i in range(0,n): 
        hash = to_int32(31 * hash) + to_ascii( (key+pwd)[i] )
            
    return hash


