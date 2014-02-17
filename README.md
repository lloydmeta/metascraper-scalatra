# Metascraper Scalatra # [![Build Status](https://travis-ci.org/lloydmeta/metascraper-scalatra.png?branch=master)](https://travis-ci.org/lloydmeta/metascraper-scalatra)

This is a [Metascraper](https://github.com/lloydmeta/metascraper) API service built on [Scalatra](http://www.scalatra.org/).

Mostly done as a learning exercise, but if it happens to be useful to you, that is a nice by-product. Similar to [metascraper-service](https://github.com/lloydmeta/metascraper-service),
[metascraper-scalatra[(https://github.com/lloydmeta/metascraper-scalatra) is completely non-blocking.

__Note__ Requires (and tested against) at least JVM 7 and Scala 2.10.x

## Install and see stuff

1. Make sure you have [Memcached](http://memcached.org/) installed
2. Clone this project and enter that directory
3. Run `./sbt`
4. From within the sbt prompt, enter `container:start`
5. Go to the built-in Swagger documentation at [localhost:8080/swagger](http://localhost:8080/swagger) and play round

## Vagrant

If you happen to use Vagrant and don't like to dirty your host machine with things like Memcached, you're in luck.

The following steps assume you have Vagrant set up properly along with some kind of Ruby interpreter.

1. Go into the `vaygrant` directory (note the y)
2. Run `bundle && librarian-chef install`
3. `vagrant up && vagrant ssh` to bring provision the VM and SSH into it. This could take a while.
4. Once you're in the VM, go to `/metascraper` and start from [step 3](#install-and-see-stuff)

## Heroku deployment

This Scalatra project is ready to be deployed to Heroku as-is.

1. Create a Heroku app (either via the web dashboard UI or your heroku commandline tool)
2. Add a config variable of `ENV` that equals `production` (e.g. `heroku config:set ENV=production`
3. Provision a Memcachier add-on (if not using Memcachier, modify `application.conf` to point to the proper variables
4. Push as normal

## Licence

The MIT License (MIT)

Copyright (c) 2013 by Lloyd Chan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
