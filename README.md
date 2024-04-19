## OVERVIEW
This is a program that will deduplicate and organize directories of files. It was designed for use with media such as a collection of photos and videos downloaded from cloud services. If you use several cloud services you may end up with the same photo stored in multiple places, using different file names. This tool is designed to organize these files when you have them locally, such as if they are all downloaded to a local NAS.

The deduplicate functionality traverses all of the files in the input directories, calculating an MD5 checksum for each file and copying the deduplicated files to a new location. Note, this functionality uses an internal hash map keyed by the MD5 checksum, so its memory usage will scale by the number of files you have. Keep this in mind if you are running this on a low memory device. 

The organize functionality organizes the files into directories using a selectable date format of YYYY-MM-DD or DD-MM-YYYY. When organizing, the tool determines the date by first looking for a matching date format in the path of the file. If one can't be found, it uses the last modified date of the file. All of this can be seen in the DateOrganizer class and accompanying tests.


## USAGE
To get a list of the command line parameters and usage, start with the -h help option.

````
java -j MediaOrganizer.jar -h
````
