Este é o teste de ambiente onde é feito os diversos testes

este ambiente tem 3 pastas:


	input - é onde se situam os ficheiros de entrada

	output - é onde se situam a saída dos ficheiros (output do programa ou ficheiro compilado)

	expected - é onde se situam os valores de saída esperados, esses ficheiros serão comparados com os ficheiros da pasta output


os testes serão corridos por scripts feitos em bash
aqui mostra a funcionalidade de cada script:


	saida_arvore.sh - apenas corre o programa gravando as entradas para as saidas (ficheiros com nome arvore*.dif)

	arvore_unit_test.sh - faz o teste unitarios com os ficheiros nomeados por arvore*.dif


todas as saidas do teste serão gravados para um ficheiro "results.txt" que depois será lido automaticamente pela linha de comando

