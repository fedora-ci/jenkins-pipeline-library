#!/usr/bin/groovy


def call(Map params = [:]) {

    def name = params.get('name', 'World')
    def msg = 'Hello ' + name + '!'
    println msg

    return msg
}
