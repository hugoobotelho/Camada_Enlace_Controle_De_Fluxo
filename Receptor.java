/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 20/05/2023
* Ultima alteracao.: 31/05/2023
* Nome.............: Camada de Enlace de dados Controle de erro
* Funcao...........: Simular a camada enlace de dados de uma rede
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;

public class Receptor {
  private int tipoDeDecodificacao = 0;
  private int tipoDeEnquadramento = 0;
  TextArea text = new TextArea();
  String buffer = "";
  int qtdBitsTotais = 0;
  int tipoControleErro = 0;
  boolean detectouErro = false;
  private int tipoControleFluxo = 0;
  // private int frame_expected = 0;
  private String[] frame_expected = { "00", "01", "10", "11" };
  private int indiceNextFrame = 0;
  private String[] ackJanela1Bit = { "00", "01", "10", "11" };
  private int indiceAck = 0;

  public Receptor() {
    Platform.runLater(() -> {
      text.setLayoutX(918);
      text.setLayoutY(242);
      text.setStyle(
          "-fx-font-size: 14px;" + // Tamanho da fonte
              "-fx-border-radius: 10px;" + // Bordas arredondadas
              "-fx-padding: 10px;" + // Padding
              "-fx-background-color: #435D7A;" + // Cor de fundo
              "-fx-focus-color: transparent;" + // Cor de foco
              "-fx-faint-focus-color: transparent;" // Cor de foco fraco
      );
      // Definindo o tamanho do TextArea e setando a mensagem
      text.setPrefSize(145, 90);
      text.setEditable(false);
      Principal.root.getChildren().add(text);
    });

  }

  /*
   * ***************************************************************
   * Metodo: setTipoDeCodificacao.
   * Funcao: metodo para setar tipo de codificacao da mensagem.
   * Parametros: recebe uma quantidade de caracters do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setTipoDeCodificacao(int codificacao) {
    this.tipoDeDecodificacao = codificacao;
  }

  /*
   * ***************************************************************
   * Metodo: setTipoDeEnquadramento.
   * Funcao: metodo para setar tipo de enquadramento da mensagem.
   * Parametros: recebe o tipo do enquadramento.
   * Retorno: sem retorno.
   */
  public void setTipoDeEnquadramento(int tipoDeEnquadramento) {
    this.tipoDeEnquadramento = tipoDeEnquadramento;
  }

  /*
   * ***************************************************************
   * Metodo: zeraBuffer.
   * Funcao: metodo para zerar o buffer para a nova mensagem que chegar.
   * Parametros: rao recebe nada.
   * Retorno: sem retorno.
   */
  public void zeraBuffer() {
    this.buffer = "";
  }

  /*
   * ***************************************************************
   * Metodo: setQtdBitsTotais.
   * Funcao: metodo para inserir a quantidade de bits totais.
   * Parametros: recebe a quantidade de bits totais do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setQtdBitsTotais(int qtdBitsTotais) {
    this.qtdBitsTotais = qtdBitsTotais;
  }

  /*
   * ***************************************************************
   * Metodo: setTipoControleErro.
   * Funcao: metodo para inserir o tipo de controle do erro.
   * Parametros: recebe a o tipo de controle do erro do tipo inteiro.
   * Retorno: sem retorno.
   */
  public void setTipoControleErro(int n) {
    this.tipoControleErro = n;
  }

  public void setTipoControleFluxo(int n) {
    this.tipoControleFluxo = n;
  }

  private void nextFrameExpected() {
    indiceNextFrame++;
    if (indiceNextFrame > 3) {
      indiceNextFrame = 0;
    }
  }

  private void nextAck() {
    indiceAck++;
    if (indiceAck > 3) {
      indiceAck = 0;
    }
  }

  private String retiraNumDeSerieDoQuadro(int[] quadro) {
    String num = "";
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (i >= qtdBitsTotais - 2) { // pega os dois bits do numero de sequencia do quadro
        if (bit == 1) {
          num += '1';
        } else {
          num += '0';
        }
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
    }
    System.out.println("Esse e o numero de sequencia do quadro " + num);
    System.out.println("Esse e o quadro no num serie: ");
    for (int i = 0; i < quadro.length; i++) {
      System.out.println(String.format("%32s",
          Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    }
    return num;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaReceptora.
   * Funcao: metodo para chamar a codificacao necessaria para decodificar a
   * mensagem com base no tipo de codificacao e depois chamar a
   * CamadaDeAplicacaoReceptora.
   * Parametros: recebe um array do tipo inteiro referenete a mensagem codificada.
   * Retorno: sem retorno.
   */
  public void CamadaFisicaReceptora(int fluxoBrutoDeBits[]) {
    // se o tipo de enquadramento for violacao, entao primeiro vai chamar o controle
    // de erro, o controle de erro vai chamar a decodificacao fisica e a
    // decodificacao fisica vai chamar a camada de apresentacaos
    int[] quadro = new int[0]; // ATENÇÃO: trabalhar com BITS!!!
    if (tipoDeEnquadramento != 3) {
      switch (tipoDeDecodificacao) {
        case 0: // codificao binaria
          quadro = CamadaFisicaReceptoraDecodificacaoBinaria(fluxoBrutoDeBits);
          break;
        case 1: // codificacao manchester
          // if (tipoDeEnquadramento == 3) {
          // fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
          // }
          quadro = CamadaFisicaReceptoraDecodificacaoManchester(fluxoBrutoDeBits);
          break;
        case 2: // codificacao manchester diferencial
          // if (tipoDeEnquadramento == 3) {
          // fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
          // }
          quadro = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
          break;
      }// fim do switch/case
       // chama proxima camada
       // System.out.println("Esse é o quadro na camada fisica receptora
       // decodificado");
       // for (int i = 0; i < quadro.length; i++) {
       // System.out.println(String.format("%32s",
       // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
       // }
      CamadaEnlaceDadosReceptora(quadro);
    } else { // se o tipo de enquadramento for violacao de camada
      CamadaEnlaceDadosReceptora(fluxoBrutoDeBits);
    }
    // CamadaDeAplicacaoReceptora(quadro);
  }// fim do metodo CamadaFisicaTransmissora

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptora.
   * Funcao: metodo para chamar o controle de erro
   * Parametros: recebe um array do tipo inteiro referenete a mensagem
   * decodificada.
   * Retorno: sem retorno.
   */
  public void CamadaEnlaceDadosReceptora(int quadro[]) {
    CamadaEnlaceDadosReceptoraControleDeErro(quadro);
    // CamadaEnlaceDadosReceptoraDesenquadramento(quadro);
    // CamadaEnlaceDadosReceptoraControleDeFluxo();
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErro.
   * Funcao: metodo para chamar o algoritmo de controle de erro para verificar se
   * detectou o erro
   * Parametros: recebe um array do tipo inteiro
   * Retorno: sem retorno.
   */
  public void CamadaEnlaceDadosReceptoraControleDeErro(int quadro[]) {
    int[] quadroControleErro = new int[quadro.length];
    switch (tipoControleErro) {
      case 0: // bit de paridade par
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(quadro);
        break;
      case 1: // bit de paridade impar
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(quadro);
        break;
      case 2: // CRC
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroCRC(quadro);
        break;
      case 3: // codigo de hamming
        quadroControleErro = CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(quadro);
        break;
    }// fim do switch/case
    System.out.println("Essa e a qtd de bits totais tirando os bits de controle: " + qtdBitsTotais);
    if (detectouErro == false) { // se nao detectou erro, segue o fluxo
      System.out.println("Nao detectou erro");
      if (tipoDeEnquadramento != 3) {
        CamadaEnlaceDadosReceptoraDesenquadramento(quadroControleErro);
      } else {
        switch (tipoDeDecodificacao) {
          case 0: // codificao binaria
            quadroControleErro = CamadaFisicaReceptoraDecodificacaoBinaria(quadroControleErro);
            break;
          case 1: // codificacao manchester
            if (tipoDeEnquadramento == 3) {
              quadroControleErro = DecodificacaoViolacaoCamadaFisica(quadroControleErro);
            }
            quadroControleErro = CamadaFisicaReceptoraDecodificacaoManchester(quadroControleErro);
            break;
          case 2: // codificacao manchester diferencial
            if (tipoDeEnquadramento == 3) {
              quadroControleErro = DecodificacaoViolacaoCamadaFisica(quadroControleErro);
            }
            quadroControleErro = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadroControleErro);
            break;
        }// fim do switch/case
        CamadaEnlaceDadosReceptoraControleDeFluxo(quadroControleErro);
        // CamadaDeAplicacaoReceptora(quadroControleErro);
      }
    } else {
      System.out.print("Detectou erro");
    }

  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErro

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar.
   * Funcao: metodo para detectar o erro bit de paridade par
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro controle erro.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(int quadro[]) {
    // implementacao do algoritmo para VERIFICAR SE HOUVE ERRO
    int[] quadroControleErro = new int[quadro.length];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        qtdBitsUm++;
        if (i != qtdBitsTotais - 1) {
          quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
              | (1 << deslocaQuadroControleErro);
        }
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for
    if (qtdBitsUm % 2 == 0) { // se a quantiadade de bits 1 for par, significa que nao detectou erro
      detectouErro = false;
      // Mostrar na tela que nao detectou erro
    } else {
      detectouErro = true;
      // Mostrar na tela que detectou erro
    }
    deslocaQuadroControleErro--;
    qtdBitsTotais--;

    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar.
   * Funcao: metodo para detectar o erro bit de paridade impar
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro controle erro.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(int quadro[]) {
    // implementacao do algoritmo para VERIFICAR SE HOUVE ERRO
    int[] quadroControleErro = new int[quadro.length];
    int deslocaQuadroControleErro = 31;
    int indexQuadroControleErro = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int qtdBitsUm = 0;
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        qtdBitsUm++;
        if (i != qtdBitsTotais - 1) {
          quadroControleErro[indexQuadroControleErro] = quadroControleErro[indexQuadroControleErro]
              | (1 << deslocaQuadroControleErro);
        }
      }
      deslocaQuadroControleErro--;
      if (deslocaQuadroControleErro < 0) {
        deslocaQuadroControleErro = 31;
        indexQuadroControleErro++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      // System.out.println("Mudou o index de quadro");
      if (indexQuadro >= quadro.length || indexQuadroControleErro >= quadroControleErro.length) {
        break;
      }
    } // fim do for
    // System.out.println(qtdBitsUm);
    if (qtdBitsUm % 2 != 0) { // se a quantiadade de bits 1 for impar, significa que nao detectou erro
      detectouErro = false;
      // Mostrar na tela que nao detectou erro
    } else {
      detectouErro = true;
      // Mostrar na tela que detectou erro
    }
    deslocaQuadroControleErro--;
    qtdBitsTotais--;

    return quadroControleErro;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroCRC.
   * Funcao: metodo para detectar o erro CRC
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro resto.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroCRC(int quadro[]) {
    // implementacao do algoritmo
    String crc32 = "100000100110000010001110110110111";
    char[] arrayDeCaracteres = crc32.toCharArray();
    int crc32Mascara = 0;
    int deslocaCRC32Mascara = 31;
    // cria a mascara crc32
    for (int i = 0; i < arrayDeCaracteres.length - 1; i++) {
      char caractere = crc32.charAt(i);
      if (caractere == '1') {
        crc32Mascara = crc32Mascara | (1 << deslocaCRC32Mascara);
      }
      deslocaCRC32Mascara--;
    } // terminou de criar a mascara do CRC32
    deslocaCRC32Mascara = 31;
    // System.out.println("CRC32");
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(crc32Mascara)).replace(' ', '0'));

    int[] quadroResto = new int[quadro.length * 2];
    int deslocaResto = 31;
    int indexResto = 0;
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    // coloca os bits de quadro em quadroResto
    for (int i = 0; i < qtdBitsTotais; i++) {
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for

    indexResto = 0;
    deslocaResto = 31;
    for (int i = 0; i < qtdBitsTotais; i++) { // faz a divisao modulo 2 (XOR) para descobri o resto
      int bit = (quadroResto[indexResto] >> deslocaResto) & 1;
      int deslocaRestoAux = deslocaResto;
      int indexRestoAux = indexResto;
      int deslocaCRC32MascaraAux = 31; // Vamos percorrer do bit mais significativo ao menos significativo do CRC32

      if (bit == 1) {
        for (int j = 0; j < 32; j++) {
          // System.out.println("Desloca resto: " + deslocaRestoAux + "\nIndex resto aux:
          // " + indexRestoAux);
          // System.out.println("Fazendo o XOR");

          int bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
          // System.out.println("Bit do quadro: " + bitQuadroResto);

          int bitCRC = (crc32Mascara >> deslocaCRC32MascaraAux) & 1;
          // System.out.println("Bit do crc: " + bitCRC);

          int xorResult = bitQuadroResto ^ bitCRC;
          // System.out.println("XOR " + xorResult);

          // Limpa o bit na posição atual e insere o bit resultante do XOR
          quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
              | (xorResult << deslocaRestoAux);

          deslocaRestoAux--;
          deslocaCRC32MascaraAux--;

          if (deslocaRestoAux < 0) {
            deslocaRestoAux = 31;
            indexRestoAux++;
          }
          if (deslocaCRC32MascaraAux < 0) {
            // Fazer XOR com o bit implícito 1 do CRC-32
            bitQuadroResto = (quadroResto[indexRestoAux] >> deslocaRestoAux) & 1;
            xorResult = bitQuadroResto ^ 1; // XOR com 1
            quadroResto[indexRestoAux] = (quadroResto[indexRestoAux] & ~(1 << deslocaRestoAux))
                | (xorResult << deslocaRestoAux);

            deslocaRestoAux--;
            if (deslocaRestoAux < 0) {
              deslocaRestoAux = 31;
              indexRestoAux++;
            }

            // Reseta o deslocamento da máscara do CRC para começar do bit mais
            // significativo novamente
            deslocaCRC32MascaraAux = 31;
          }
        }
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
    }

    qtdBitsTotais += 32;
    deslocaQuadro = 31;
    indexQuadro = 0;
    deslocaResto = 31;
    indexResto = 0;

    for (int i = 0; i < qtdBitsTotais; i++) { // verifica se o resto deu 0, senao deu e pq houve erro
      int bit = (quadroResto[indexResto] >> deslocaResto) & 1;
      if (bit == 1) {
        // System.out.println("\nDetectou o erro!\n");
        detectouErro = true;
        break;
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      if (indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for

    // System.out.println("Esse é o quadro Resto");
    // for (int i = 0; i < quadroResto.length; i++){
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }

    qtdBitsTotais -= 64;
    // System.out.println(qtdBitsTotais);
    indexQuadro = 0;
    indexResto = 0;
    deslocaQuadro = 31;
    deslocaResto = 31;
    for (int i = 0; i < qtdBitsTotais; i++) { // coloca os bits da carga util de quadro em quadroResto para enviar a
                                              // mensagem agora SEM o resto
      int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
      if (bit == 1) {
        quadroResto[indexResto] = quadroResto[indexResto] | (1 << deslocaResto);
      }
      deslocaResto--;
      if (deslocaResto < 0) {
        deslocaResto = 31;
        indexResto++;
      }
      deslocaQuadro--;
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      if (indexQuadro >= quadro.length || indexResto >= quadroResto.length) {
        break;
      }
    } // fim do for
    // System.out.println("Essa e a mensagem SEM o Resto no CRC");
    // for (int i = 0; i < quadroResto.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroResto[i])).replace(' ', '0'));
    // }

    return quadroResto;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroCRC

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming.
   * Funcao: metodo para detectar o erro Codigo de Hamming
   * Parametros: recebe um array do tipo inteiro
   * Retorno: retorna o quadro Hamming.
   */
  public int[] CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(int quadro[]) {
    // implementacao do algoritmo // implementacao do algoritmo para VERIFICAR SE
    // HOUVE ERRO
    int[] quadroHamming = new int[quadro.length * 3];
    int indexQuadroHamming = 0;
    int deselocaQuadroHamming = 31;
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int expoente = 0;
    // System.out.println("Essa e o quadro com informacao de controle");
    // for (int i = 0; i < quadro.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }
    // deselocaQuadroHamming = 31;
    // verificar os valores dos bits de paridade
    for (int i = 0; i < 7; i++) {
      double posBitParidade = ((Math.pow(2, i)) - 1);
      // System.out.println("Pos bit paridade " + posBitParidade);
      int qtdUm = 0;
      deslocaQuadro = 31 - (int) posBitParidade;
      // System.out.println("Desloca quadro: " + deslocaQuadro);
      if (posBitParidade > 31) {
        deslocaQuadro = 0;
        indexQuadro = 1;
      }
      // System.out.println("Desloca quadro hamming " + deselocaQuadroHamming);
      int indexQuadroAux = indexQuadro;
      for (int j = 0; j < qtdBitsTotais * 4; j++) { // vai verificar todos os bits relacionados ao bit de paridade
        // System.out.println("Parte da posicao " + deslocaQuadro);
        for (int k = 0; k < (int) (posBitParidade) + 1; k++) { // conta
          int bit = (quadro[indexQuadroAux] >> deslocaQuadro) & 1;
          // System.out.println(bit);
          if (bit == 1) {
            qtdUm++;
          }
          deslocaQuadro--;
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31 + deslocaQuadro + 1;
            indexQuadroAux++;
          }
          if (indexQuadroAux >= quadro.length) {
            break;
          }
        }
        for (int k = 0; k < (int) (posBitParidade) + 1; k++) { // pula
          deslocaQuadro--;
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31 + deslocaQuadro + 1;
            indexQuadroAux++;
          }
          if (indexQuadroAux >= quadro.length) {
            break;
          }
        }
        // deselocaQuadroHamming -= i+1; //pula a quantidade necessaria para somar os
        // proximos bits
        // System.out.println("Pulou para " + deselocaQuadroHamming);
        // if (deselocaQuadroHamming < 0){
        // deselocaQuadroHamming = 31 + deselocaQuadroHamming + 1;
        // indexQuadroHammingAux++;
        // }
        if (indexQuadroAux >= quadro.length) {
          break;
        }
      }
      if (qtdUm % 2 == 1) { // se a quantidadde de 1 (incluindo a informacao de controle) nao for par, entao
                            // deu erro
        // System.out.println("\n\nDetectou ERRO! na iteracao " + i + "\n\n");
        detectouErro = true;
      }
      // System.out.println(qtdUm);
    }

    // retira os bits de informacao de controle de quadroHamming
    indexQuadro = 0;
    indexQuadroHamming = 0;
    deslocaQuadro = 31;
    deselocaQuadroHamming = 31;
    for (int i = 0; i < qtdBitsTotais + 64; i++) {
      double potencia = Math.pow(2, expoente) - 1;
      if (i == (int) potencia) { // se a posicao for igual a uma potencia de 2 (menos 1, pq o array comeca de 0),
                                 // entao apenas pula a posicao
        expoente++;
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
      } else { // se nao, insere o bit de quadro na devida posicao de quadroHamming
        int bit = (quadro[indexQuadro] >> deslocaQuadro) & 1;
        if (bit == 1) {
          quadroHamming[indexQuadroHamming] = quadroHamming[indexQuadroHamming] | (1 << deselocaQuadroHamming);
        }
        deselocaQuadroHamming--;
        if (deselocaQuadroHamming < 0) {
          deselocaQuadroHamming = 31;
          indexQuadroHamming++;
        }
        deslocaQuadro--;
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
      }
      if (indexQuadro >= quadro.length || indexQuadroHamming >= quadroHamming.length) {
        break;
      }
    } // fim do for
    // System.out.println("Essa e o quadro Hamming");
    // for (int i = 0; i < quadroHamming.length; i++) {
    // System.out.println(String.format("%32s",
    // Integer.toBinaryString(quadroHamming[i])).replace(' ', '0'));
    // }
    qtdBitsTotais -= 7;
    return quadroHamming;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramento.
   * Funcao: metodo para fazer o desenquadramento com base no escolhido pelo
   * usuario
   * Parametros: recebe um array do tipo inteiro
   * Retorno: sem retorno.
   */
  public void CamadaEnlaceDadosReceptoraDesenquadramento(int quadro[]) {
    int quadroDesenquadrado[] = new int[0]; // mudar depoi
    String sequencia = retiraNumDeSerieDoQuadro(quadro);
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        quadroDesenquadrado = quadro;
        break;
    }// fim do switch/case
     // CamadaDeAplicacaoReceptora(quadroDesenquadrado);
    CamadaEnlaceDadosReceptoraControleDeFluxo(quadroDesenquadrado);
  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaReceptoraDecodificacaoBinaria.
   * Funcao: metodo para decodificar a mensagem binaria.
   * Parametros: recebe o array de inteiros.
   * Retorno: retorna um array de interios decodificado.
   */
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int fluxoBrutoDeBits[]) {
    int[] quadro = new int[fluxoBrutoDeBits.length];
    quadro = fluxoBrutoDeBits;
    /*
     * int indexQuadro = 0;
     * for (int i = 0; i < fluxoBrutoDeBits.length; i++){
     * for (int j = 31; j >= 0; j--){
     * int bit = (fluxoBrutoDeBits[i] >> j) & 1;
     * quadro[indexQuadro] = quadro[indexQuadro] | (bit << j);
     * }
     * indexQuadro++;
     * }
     */
    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaTransmissoraCodificacaoMancherster.
   * Funcao: metodo para decodificar a mensagem em Mancherster (cada par O1
   * representa o 0 e cada para 10 representa o 1).
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array decodificado.
   */
  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int fluxoBrutoDeBits[]) {
    int[] quadro = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFLuxo = 0;
    int deslocaFluxo = 31;
    for (int i = 0; i < qtdBitsTotais; i++) { // qtdBitsTotais
      int posAnterior = deslocaFluxo;
      int posSucessor = deslocaFluxo - 1;
      int bitAnterior = (fluxoBrutoDeBits[indexFLuxo] >> posAnterior) & 1;
      int bitSucessor = (fluxoBrutoDeBits[indexFLuxo] >> posSucessor) & 1;
      if (bitAnterior == 1 && bitSucessor == 0) {
        quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 0 && bitSucessor == 1) {
        quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 1 && bitSucessor == 1) { // igora o par 11 caso o enquadramento seja violacao de camada
        i++;
        /*
         * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
         * deslocaQuadro--;
         * if (deslocaQuadro < 0){
         * deslocaQuadro = 31;
         * indexQuadro++;
         * }
         * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
         */
      }
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      deslocaFluxo -= 2;
      if (deslocaFluxo < 0) {
        deslocaFluxo = 31;
        indexFLuxo++;
      }
      if (indexFLuxo >= fluxoBrutoDeBits.length || indexQuadro >= quadro.length) {
        break;
      }

    }
    /*
     * System.out.println("Esse e o quadro decodificado Mancherster");
     * for (int i = 0; i < quadro.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadro[i])).replace(' ', '0'));
     * }
     */
    qtdBitsTotais = qtdBitsTotais / 2;
    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaFisicaReceptorarDecodificacaoManchersterDiferencial.
   * Funcao: metodo para decodificar a mensagem em ManchersterDiferencial (onda a
   * inversao de sinal representa o 0 e a falta de inversao de sinall representa o
   * 1).
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array decodificado.
   */
  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int fluxoBrutoDeBits[]) {
    // o problema acontece quando ele le uma sequencia de 1111 1 que seria a flag e
    // o proximo bit
    int[] quadro = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFluxo = 0;
    int deslocaFluxo = 29;
    int primeiroBit = (fluxoBrutoDeBits[0] >> 31) & 1;
    int segundoBit = (fluxoBrutoDeBits[0] >> 30) & 1;
    // if (tipoDeEnquadramento == 3) {
    // fluxoBrutoDeBits = DecodificacaoViolacaoCamadaFisica(fluxoBrutoDeBits);
    // }
    for (int i = 0; i < fluxoBrutoDeBits.length * 32; i++) { // qtdBitsTotais
      if (i == 0) { // se esta na primeira iteracao
        if (primeiroBit == 1 && segundoBit == 0) {
          quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          deslocaQuadro--;
        } else if (primeiroBit == 0 && segundoBit == 1) {
          quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
          deslocaQuadro--;
        } else if (primeiroBit == 1 && segundoBit == 1) {
          i++;
          /*
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * if (((fluxoBrutoDeBits[0] >> 29) & 1)==1 & ((fluxoBrutoDeBits[0] >> 28) & 1)
           * == 0){
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * }
           * else if (((fluxoBrutoDeBits[0] >> 29) & 1)==0 & ((fluxoBrutoDeBits[0] >> 28)
           * & 1) == 1){
           * quadro[indexQuadro] = quadro[indexQuadro] | (0<<deslocaQuadro);
           * }
           */
        }
      } else {
        int posAnterior = deslocaFluxo + 1;
        int posSucessor = deslocaFluxo;
        int proximo = deslocaFluxo - 1;
        int bitAnterior = (fluxoBrutoDeBits[indexFluxo] >> posAnterior) & 1;
        int bitSucessor = (fluxoBrutoDeBits[indexFluxo] >> posSucessor) & 1;
        int bitProximo = (fluxoBrutoDeBits[indexFluxo] >> proximo) & 1;
        if (bitSucessor == 1 && bitProximo == 1) { // verifica se achou o par 11 (serve para a violacao de camada)
          i++;
          /*
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * if (deslocaQuadro < 0){
           * deslocaQuadro = 31;
           * indexQuadro++;
           * }
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           */
        } else if (bitSucessor == 0 && bitProximo == 0) {
          // deslocaQuadro--;
        } else if ((bitAnterior == bitSucessor)) {
          quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          deslocaQuadro--;
        } else if (bitAnterior != bitSucessor) {
          quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
          deslocaQuadro--;
        }
        if (deslocaQuadro < 0) {
          deslocaQuadro = 31;
          indexQuadro++;
        }
        deslocaFluxo -= 2;
        if (deslocaFluxo <= 0) {
          posAnterior = 0;
          bitAnterior = (fluxoBrutoDeBits[indexFluxo] >> posAnterior) & 1;
          indexFluxo++;
          if (indexFluxo >= fluxoBrutoDeBits.length) {
            break;
          }
          posSucessor = 31;
          bitSucessor = (fluxoBrutoDeBits[indexFluxo] >> posSucessor) & 1;
          proximo = 30;
          bitProximo = (fluxoBrutoDeBits[indexFluxo] >> proximo) & 1;
          /*
           * if (bitSucessor == 1 && bitProximo == 1){ //verifica se achou o par 11 (serve
           * para a violacao de camada)
           * i++;
           * /*
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * deslocaQuadro--;
           * if (deslocaQuadro < 0){
           * deslocaQuadro = 31;
           * indexQuadro++;
           * }
           * quadro[indexQuadro] = quadro[indexQuadro] | (1<<deslocaQuadro);
           * 
           * }
           * else
           */if (bitSucessor == 0 && bitProximo == 0) {
            // deslocaQuadro--;
          } else if ((bitAnterior == bitSucessor)) {
            quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            deslocaQuadro--;
          } else if (bitAnterior != bitSucessor) {
            quadro[indexQuadro] = quadro[indexQuadro] | (0 << deslocaQuadro);
            deslocaQuadro--;
          }
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31;
            indexQuadro++;
          }
          deslocaFluxo = 29;
        }
        if (indexFluxo >= fluxoBrutoDeBits.length | indexQuadro >= quadro.length) {
          break;
        }
      }
    }
    /*
     * System.out.println("Esse e o quadro descodificado Mancherster Diferencial");
     * for (int i = 0; i < quadro.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadro[i])).replace(' ', '0'));
     * }
     */
    qtdBitsTotais = qtdBitsTotais / 2;
    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres.
   * Funcao: metodo para desenquadrar a mensagem do tipo Contagem de Caracteres.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] CamadaEnlaceDadosReceptoraDesenquadramentoContagemDeCaracteres(int[] quadroEnquadrado) {
    int[] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    String contador4 = "00000100"; // represeta o 4 em binario, dessa forma, estou dizendo que vai contar de 4 em 4
    String contador3 = "00000011"; // representa o 3
    String contador2 = "00000010"; // representa o 2
    String aux = "";
    int qtdProximaIteracao = 0;
    for (int i = 0; i < quadroEnquadrado.length * 32; i++) { // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (bit == 1) {
        aux += '1';
      } else {
        aux += '0';
      }
      if (aux.equals(contador4)) {
        qtdProximaIteracao = 24;
      } else if (aux.equals(contador3)) {
        qtdProximaIteracao = 16;
      } else if (aux.equals(contador2)) {
        qtdProximaIteracao = 8;
      }
      if (aux.length() == 8) { // verifica se ja leu a informacao de controle completa
        for (int j = 0; j < qtdProximaIteracao; j++) {
          bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
          if (bit == 1) {
            quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
          }
          deslocaQuadro--;
          if (deslocaQuadro < 0) {
            deslocaQuadro = 31;
            indexQuadro++;
          }
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
          if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
            break;
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
        break;
      }
    }

    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes.
   * Funcao: metodo para desenquadrar a mensagem do tipo Insercao de Bytes.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBytes(int[] quadroEnquadrado) {
    int[] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    String flag = "00111111";
    String esc = "01000000";
    String aux = "";
    for (int i = 0; i < quadroEnquadrado.length * 32; i++) { // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (bit == 1) {
        aux += '1';
      } else {
        aux += '0';
      }
      if (aux.length() == 8) { // verifica se ja leu a informacao de controle completa
        if (aux.equals(esc)) { // insere os proximos 8 bits em quadro
          for (int j = 0; j < 8; j++) {
            bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
            if (bit == 1) {
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0) {
              deslocaQuadro = 31;
              indexQuadro++;
            }
            deslocaQuadoEnquadrado--;
            if (deslocaQuadoEnquadrado < 0) {
              deslocaQuadoEnquadrado = 31;
              indexQuadroEnquadrado++;
            }
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
              break;
            }
          }
        } else if (aux.equals(flag)) { // ignora
        } else if (aux.equals("00000000")) {
          break;
        } else {
          for (int j = 0; j < 8; j++) {
            if (aux.charAt(j) == '1') {
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0) {
              deslocaQuadro = 31;
              indexQuadro++;
            } /*
               * deslocaQuadoEnquadrado--;
               * if (deslocaQuadoEnquadrado<0){
               * deslocaQuadoEnquadrado = 31;
               * indexQuadroEnquadrado++;
               * }
               */
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
              break;
            }
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
        break;
      }
    }

    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits.
   * Funcao: metodo para desenquadrar a mensagem do tipo Insercao de Bits.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] CamadaEnlaceDadosReceptoraDesenquadramentoInsercaoDeBits(int[] quadroEnquadrado) {
    int[] quadro = new int[quadroEnquadrado.length];
    int deslocaQuadro = 31;
    int indexQuadro = 0;
    int deslocaQuadoEnquadrado = 31;
    int indexQuadroEnquadrado = 0;
    int qtdBitsUm = 0;
    String flag = "01111110";
    String aux = "";
    for (int i = 0; i < quadroEnquadrado.length * 32; i++) { // qtdBitsTotais
      int bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
      deslocaQuadoEnquadrado--;
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (bit == 1) {
        aux += '1';
        qtdBitsUm++;
        bit = (quadroEnquadrado[indexQuadroEnquadrado] >> deslocaQuadoEnquadrado) & 1;
        if (qtdBitsUm == 5 && bit == 0) {
          qtdBitsUm = 0;
          deslocaQuadoEnquadrado--;
          if (deslocaQuadoEnquadrado < 0) {
            deslocaQuadoEnquadrado = 31;
            indexQuadroEnquadrado++;
          }
        }
      } else {
        aux += '0';
        qtdBitsUm = 0;
      }
      if (aux.length() == 8) { // verifica se ja leu a informacao de controle completa
        if (aux.equals(flag)) {
          // ignora
        } else if (aux.equals("00000000")) {
          break;
        } else {
          for (int j = 0; j < 8; j++) {
            if (aux.charAt(j) == '1') {
              quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocaQuadro);
            }
            deslocaQuadro--;
            if (deslocaQuadro < 0) {
              deslocaQuadro = 31;
              indexQuadro++;
            } /*
               * deslocaQuadoEnquadrado--;
               * if (deslocaQuadoEnquadrado<0){
               * deslocaQuadoEnquadrado = 31;
               * indexQuadroEnquadrado++;
               * }
               */
            if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
              break;
            }
          }
        }
        aux = "";
      }
      if (deslocaQuadoEnquadrado < 0) {
        deslocaQuadoEnquadrado = 31;
        indexQuadroEnquadrado++;
      }
      if (indexQuadro >= quadro.length || indexQuadroEnquadrado >= quadroEnquadrado.length) {
        break;
      }
    }

    return quadro;
  }

  /*
   * ***************************************************************
   * Metodo: DecodificacaoViolacaoCamadaFisica.
   * Funcao: metodo para desenquadrar a mensagem do tipo Violacao de Camada.
   * Parametros: recebe o array de inteiros.
   * Retorno: retornar o array desenquadrado.
   */
  public int[] DecodificacaoViolacaoCamadaFisica(int[] fluxoBrutoDeBits) {
    int[] quadroDesenquadrado = new int[fluxoBrutoDeBits.length];
    int indexQuadro = 0;
    int deslocaQuadro = 31;
    int indexFLuxo = 0;
    int deslocaFluxo = 31;
    for (int i = 0; i < fluxoBrutoDeBits.length * 32; i++) { // qtdBitsTotais
      int posAnterior = deslocaFluxo;
      int posSucessor = deslocaFluxo - 1;
      int bitAnterior = (fluxoBrutoDeBits[indexFLuxo] >> posAnterior) & 1;
      int bitSucessor = (fluxoBrutoDeBits[indexFLuxo] >> posSucessor) & 1;
      if (bitAnterior == 1 && bitSucessor == 0) {
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (1 << deslocaQuadro);
        deslocaQuadro--;
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (0 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 0 && bitSucessor == 1) {
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (0 << deslocaQuadro);
        deslocaQuadro--;
        quadroDesenquadrado[indexQuadro] = quadroDesenquadrado[indexQuadro] | (1 << deslocaQuadro);
        deslocaQuadro--;
      } else if (bitAnterior == 1 && bitSucessor == 1) { // igora o par 11 caso o enquadramento seja violacao de camada
        i++;
      } else {
        break;
      }
      if (deslocaQuadro < 0) {
        deslocaQuadro = 31;
        indexQuadro++;
      }
      deslocaFluxo -= 2;
      if (deslocaFluxo < 0) {
        deslocaFluxo = 31;
        indexFLuxo++;
      }
      if (indexFLuxo >= fluxoBrutoDeBits.length || indexQuadro >= quadroDesenquadrado.length) {
        break;
      }
    }
    /*
     * System.out.println("Esse e o quadroDesenquadrado Violacao de camada");
     * for (int i = 0; i < quadroDesenquadrado.length; i++){
     * System.out.println(String.format("%32s",
     * Integer.toBinaryString(quadroDesenquadrado[i])).replace(' ', '0'));
     * }
     */
    return quadroDesenquadrado;
  }

  public void CamadaEnlaceDadosReceptoraControleDeFluxo(int quadro[]) {
    int[] quadroFluxo = new int[0];
    switch (tipoControleFluxo) {
      case 0: // protocolo de janela deslizante de 1 bit
        quadroFluxo = CamadaEnlaceDadosReceptoraJanelaDeslizanteUmBit(quadro);
        break;
      case 1: // protocolo de janela deslizante go-back-n
        // quadroFluxo = CamadaEnlaceDadosReceptoraJanelaDeslizanteGoBackN(quadro);
        break;
      case 2: // protocolo de janela deslizante com retransmissão seletiva
        // quadroFluxo =
        // CamadaEnlaceDadosReceptoraJanelaDeslizanteComRetransmissaoSeletiva(quadro);
        break;
      default:
        quadroFluxo = quadro;
        break;
    }// fim do switch/case
     // semaforoRecepcao.release();
    CamadaDeAplicacaoReceptora(quadroFluxo);
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeFluxo

  public int[] CamadaEnlaceDadosReceptoraJanelaDeslizanteUmBit(int quadro[]) {
    if ('0' != frame_expected[indiceNextFrame].charAt(1)) {
      System.out.println("Quadro chegou com erro ou quadro duplicado, ignora");
      return null;
    } else {
      // implementar logica para enviar o ack do quadro recebido
      // implementar a logica para lidar com quadros duplicados
      nextFrameExpected();
      return quadro;
    }
  }// fim do metodo CamadaEnlaceDadosReceptoraJanelaDeslizanteUmBit

  /*
   * ***************************************************************
   * Metodo: CamadaDeAplicacaoReceptora.
   * Funcao: metodo para trasnformar o array de interos decodificado em mensagem e
   * chamar a AplicacaoReceptora.
   * Parametros: recebe o array de inteiros.
   * Retorno: sem retorno.
   */
  public void CamadaDeAplicacaoReceptora(int quadro[]) {

    // for (int i = 0; i < quadro.length; i++){
    // System.out.println("Esse e o quadro CamadaDeAplicacaoReceptora "+String.
    // format("%32s", Integer.toBinaryString(quadro[i])).replace(' ', '0'));
    // }

    String mensagem = "";
    // String representando a sequência de bits
    String binaryString = "";
    int contador = 0;
    if (quadro != null) { //se houve erro ou o quadro foi duplicado, ignora
      for (int i = 0; i < quadro.length; i++) {
        for (int j = 31; j >= 0; j--) {
          int bit = (quadro[i] >> j) & 1;
          if (bit == 1) {
            binaryString += "1";
          } else {
            binaryString += "0";
          }
          contador += 1;
          if (contador == 8) {
            int intValue = Integer.parseInt(binaryString, 2);
            mensagem += (char) intValue;
            binaryString = "";
            contador = 0;
          }
        }
      }
      // System.out.println("Essa é a mensagem:" + mensagem);
      // chama proxima camada
      buffer += mensagem;
      AplicacaoReceptora(buffer);
    }

  }// fim do metodo CamadaDeAplicacaoReceptora

  /*
   * ***************************************************************
   * Metodo: AplicacaoTransmissora.
   * Funcao: fazer a mensagem aparecer na tela.
   * Parametros: recebe uma mensagem do tipo String.
   * Retorno: sem retorno.
   */
  public void AplicacaoReceptora(String buffer) {
    Platform.runLater(() -> {
      text.setText(buffer);
    });
  }// fim do metodo AplicacaoReceptora

}
