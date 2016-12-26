from os import getenv
import json

dict_out={'latestVersion': getenv("BUILD_TAG"), "url": "http://jenkins.msfjarvis.me/job/afh-browser-master/lastBuild/", "releaseNotes" : "http://jenkins.msfjarvis.me/job/afh-browser-master/"+ getenv("BUILD_NUMBER") +"/changes"}
json.dump(dict_out,open('manifest.json','w'),indent=2)
