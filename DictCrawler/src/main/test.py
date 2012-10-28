'''
Created on 2012-10-27

@author: 翁子凡
'''
#!/usr/bin/python
# -*- coding:gbk -*-
#   @Copyright(C) 
#   2012 by huangbingliang, binglianghuang@gmail.com.
#   All right reserved.
# 
##
# @file a.py
# @Brief 
# @author  huangbingliang, souldak.com
# @version 1.0
# @date 2012-09-16
import sys
import json
import re
import codecs
import urllib2


def getphonogram(word):
    phonogram = None
    try:
        html = urllib2.urlopen('http://dict.baidu.com/s?wd='+word,timeout=4).read()
        p1 = re.compile(r'<b lang="EN-US" xml:lang="EN-US">[^<]*</b>')
        m = p1.findall(html)
        pos=0
        if len(m) > 1:
            pos=1
        p2 = re.compile(r"\[.*\]")
        m = p2.findall(m[pos])
        if len(m) > 0:
            phonogram = re.sub('[\[\]]','',m[0])
                        
        print "word",word,"phonogram",phonogram      
    except:
        print "Get ",word," failed. reason:",sys.exc_info()[0]
        return None
    return phonogram

filename=sys.argv[1]
print filename

lines = open(filename).readlines()
linenum = len(lines)
clips = linenum/200 + 1
outf=open("gre_phonogram"+filename+".txt","w")
num=0;
word=""
for line in lines:
    try:
        line = line.decode('utf-8')
        line = re.split("[\[\]]",line)
        dic={}
        num+=1
        print num
        word=line[0].strip()
        retry=10
        phonogram = None
        while retry>0:
            retry-=1
            phonogram = getphonogram(word)
            if phonogram is not None:
                break
            else:
                print "retry",word
        if phonogram is not None:
            outf.write(word.encode("utf-8")+"\t"+phonogram+"\n");
            outf.flush()
        else:
            print "FAILD:get phonogram failed. word=",word
            
    except NameError:
        print "word=",word,"Unexpected error:", sys.exc_info()[0]
        pass

outf.close()


