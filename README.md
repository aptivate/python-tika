# python-tika - Python bindings for Apache Tika

## Requirements

* Java >= 1.5
* [JCC](http://lucene.apache.org/pylucene/jcc/index.html)

## Installation
	$ python setup.py build
	$ python setup.py install

## Usage

To use the `AutoDetectParser`,

	import tika
	tika.initVM()

	from tika import parser
   
	print parser.from_buffer("<html><body>Hello World</body></html>")
	# Or directly from a file, 
	# print parser.from_file("/tmp/foo.doc")
   
returns a `dict`,

	{'content': u'Hello Cruel World',
	 'metadata': {u'Content-Encoding': u'ISO-8859-1',
					  u'Content-Type': u'text/html',
					  u'title': u'Hello world'}
	}

  
   
   
   
   

