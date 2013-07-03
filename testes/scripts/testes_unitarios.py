#!/usr/bin/env python
import glob
import subprocess
import os

def subtest(type):
	outputs = []
	for filepath in glob.glob('testes/expected/arvore*.'+type+'.txt'):
			outputs.append(os.path.basename(filepath).split('.'+type+'.txt')[0])
	for filepath in glob.glob('testes/input/arvore*.dif'):
		filename = os.path.basename(filepath);
		if filename in outputs:
			print 'running %s' % filename, 
			result = subprocess.check_output(['java','dif',filepath, type])
			f = open('testes/expected/%s' % filename+'.'+type+'.txt' , 'r')
			expected = f.read()
			f.close()
			if result == expected:
				print 'test passed'
			else:
				print 'test failed'
				print "=========== result - %s ==============" % filename
				print result
				print "=========== expect - %s ==============" % filename
				print expected
				print '==========================='
		else:
			print '%s doesnt exist in expected folder' % filename

def subIRtest(type):
	outputs = []
	for filepath in glob.glob('testes/expected/arvore*.'+type+'.txt'):
			outputs.append(os.path.basename(filepath).split('.'+type+'.txt')[0])
	for filepath in glob.glob('testes/input/arvore*.difir'):
		filename = os.path.basename(filepath);
		subfile = filename.split('.')[0]+'.dif'
		if subfile in outputs:
			print 'running %s' % filename, 
			result = subprocess.check_output(['java','dif',filepath, type])
			f = open('testes/expected/%s' % subfile+'.'+type+'.txt' , 'r')
			expected = f.read()
			f.close()
			if result == expected:
				print 'test passed'
			else:
				print 'test failed'
				print "=========== result - %s ==============" % filename
				print result
				print "=========== expect - %s ==============" % filename
				print expected
				print '==========================='
		else:
			print '%s doesnt exist in expected folder' % filename


def test():
	subtest('AST')
	subtest('ST')
	subtest('IR')
	subtest('code')
	subIRtest('IR')
	subIRtest('code')
	 


		
	
if __name__ == '__main__':
	test()
		
