# language: pt
Funcionalidade: API - Orders


  Cenário: Iniciar um pedido com cliente
    Dado que quero inciar um pedido
    Quando informando cliente e produtos
    Entao devo conseguir iniciar um pedido

  Cenário: Iniciar um pedido sem cliente
    Dado que quero inciar um pedido sem cliente
    Quando informando apenas produtos
    Entao devo conseguir iniciar um pedido apenas com produto

  Cenário: Pesquisar um pedido
    Dado que quero pesquisar um pedido
    Quando informar um id
    Entao devo conseguir obter um pedido

  Cenário: Pesquisar por todos os pedidos
    Dado que pesquiso por todos os pedidos
    Entao devo conseguir obter todos os pedido

  Cenário: Pesquisar pedidos ordenados
    Dado que preciso da lista de pedidos ordenada
    Entao devo conseguir obter a lista de pedidos ordenada por data