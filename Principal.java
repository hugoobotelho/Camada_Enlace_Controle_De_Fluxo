/* ***************************************************************
* Autor............: Hugo Botelho Santana
* Matricula........: 202210485
* Inicio...........: 20/05/2023
* Ultima alteracao.: 31/05/2023
* Nome.............: Camada de Enlace de dados Controle de erro
* Funcao...........: Simular a camada enlace de dados de uma rede
*************************************************************** */

//Importacao das bibliotecas do JavaFx

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.application.Platform;

import java.util.concurrent.Semaphore;

// Classe principal que herda da classe Application
public class Principal extends Application {
  public static Pane root = new Pane();
  public static Semaphore semaforoMeioDeComunicacao = new Semaphore(1);
  public static Transmissor transmissor;
  public static MeioDeComunicao meioDeComunicao;
  public static Receptor receptor;

  public void start(Stage primaryStage) {
    Image backgroundImage = new Image("/img/background.png");
    ImageView backgroundImageView = new ImageView(backgroundImage);
    root.getChildren().add(backgroundImageView);

    transmissor = new Transmissor();
    meioDeComunicao = new MeioDeComunicao();
    receptor = new Receptor();

    // Transmissor transmissor = new Transmissor();
    transmissor.AplicacaoTransmissora();

    // Criação dos ToggleButtons para as opções de codificacao
    ToggleButton opcao1 = new ToggleButton("Codificacao Binaria");
    ToggleButton opcao2 = new ToggleButton("Codificacao Mancherster");
    ToggleButton opcao3 = new ToggleButton("Codificacao Mancherster Diferencial");

    // Criação de um ToggleGroup para garantir que apenas um botão seja selecionado
    ToggleGroup toggleGroup = new ToggleGroup();
    opcao1.setToggleGroup(toggleGroup);
    opcao2.setToggleGroup(toggleGroup);
    opcao3.setToggleGroup(toggleGroup);

    // Criação dos ToggleButtons para as opções de enquadramento
    ToggleButton contagemDeCaracteres = new ToggleButton("Contagem de Caracteres");
    ToggleButton insercaoDeBytes = new ToggleButton("Insercao de Bytes");
    ToggleButton insercaoDeBits = new ToggleButton("Insersao de Bits");
    ToggleButton violacaoDaCamadaFisica = new ToggleButton("Violacao da Camada Fisica");

    // Criação de um ToggleGroup para garantir que apenas um botão seja selecionado
    ToggleGroup toggleGroupEnquadramento = new ToggleGroup();
    contagemDeCaracteres.setToggleGroup(toggleGroupEnquadramento);
    insercaoDeBytes.setToggleGroup(toggleGroupEnquadramento);
    insercaoDeBits.setToggleGroup(toggleGroupEnquadramento);
    violacaoDaCamadaFisica.setToggleGroup(toggleGroupEnquadramento);

    // Listener para a mudança de seleção dos ToggleButtons
    toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (newValue.equals(opcao1)) {
          transmissor.tipoDeCodificacao(0);
          violacaoDaCamadaFisica.setDisable(true);
        } else if (newValue.equals(opcao2)) {
          transmissor.tipoDeCodificacao(1);
          violacaoDaCamadaFisica.setDisable(false);
        } else if (newValue.equals(opcao3)) {
          transmissor.tipoDeCodificacao(2);
          violacaoDaCamadaFisica.setDisable(false);
        }
        // Reseta as cores de todos os botões
        opcao1.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        opcao2.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        opcao3.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        // Define a cor do botão selecionado
        ((ToggleButton) newValue).setStyle(
            "-fx-font-size: 15px; -fx-background-color: #435D7A; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10;");
      }
    });

    // Define um botão como selecionado inicialmente
    toggleGroup.selectToggle(opcao1);
    VBox vboxOpoces = new VBox(10, opcao1, opcao2, opcao3);
    vboxOpoces.setAlignment(Pos.CENTER);
    vboxOpoces.setLayoutX(430);
    vboxOpoces.setLayoutY(540);

    // Listener para a mudança de seleção dos ToggleButtons
    toggleGroupEnquadramento.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        if (newValue.equals(contagemDeCaracteres)) {
          transmissor.tipoDeEnquadramento(0);
          opcao1.setDisable(false);
        } else if (newValue.equals(insercaoDeBytes)) {
          transmissor.tipoDeEnquadramento(1);
          opcao1.setDisable(false);
        } else if (newValue.equals(insercaoDeBits)) {
          transmissor.tipoDeEnquadramento(2);
          opcao1.setDisable(false);
        } else if (newValue.equals(violacaoDaCamadaFisica)) {
          transmissor.tipoDeEnquadramento(3);
          opcao1.setDisable(true);
        }
        // Reseta as cores de todos os botões
        contagemDeCaracteres.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        insercaoDeBytes.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        insercaoDeBits.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        violacaoDaCamadaFisica.setStyle(
            "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A");
        // Define a cor do botão selecionado
        ((ToggleButton) newValue).setStyle(
            "-fx-font-size: 15px; -fx-background-color: #435D7A; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10;");
      }
    });

    // Define um botão como selecionado inicialmente
    toggleGroupEnquadramento.selectToggle(contagemDeCaracteres);
    HBox hboxOpocesEnquadramento = new HBox(10, contagemDeCaracteres, insercaoDeBytes, insercaoDeBits,
        violacaoDaCamadaFisica);
    hboxOpocesEnquadramento.setAlignment(Pos.CENTER);
    hboxOpocesEnquadramento.setLayoutX(220);
    hboxOpocesEnquadramento.setLayoutY(150);

    root.getChildren().addAll(vboxOpoces, hboxOpocesEnquadramento);

    // adicionando as informacoes do Controle de erro
    Label controleErro = new Label("Controle de erro");
    controleErro.setStyle(
        "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A; -fx-background-color: #FFFFFF");

    // Criando um ComboBox e adicionando itens a ele
    ComboBox<String> dropBoxTipoControleErro = new ComboBox<>();
    dropBoxTipoControleErro.getItems().addAll("Bit de Paridade Par", "Bit de Paridade Impar", "CRC",
        "Distancia de Hamming");
    dropBoxTipoControleErro.setValue("Bit de Paridade Par");
    transmissor.tipoControleErro(0); // seta o valor de tipoControleErro inicialmente como 0
    // Adicionando um listener para o valor selecionado
    dropBoxTipoControleErro.setOnAction(event -> {
      String selectedItem = dropBoxTipoControleErro.getSelectionModel().getSelectedItem();
      if (selectedItem.equals("Bit de Paridade Par")) {
        transmissor.tipoControleErro(0);
      } else if (selectedItem.equals("Bit de Paridade Impar")) {
        transmissor.tipoControleErro(1);
      } else if (selectedItem.equals("CRC")) {
        transmissor.tipoControleErro(2);
      } else if (selectedItem.equals("Distancia de Hamming")) {
        transmissor.tipoControleErro(3);
      }
    });

    VBox vboxControleErro = new VBox(5, controleErro, dropBoxTipoControleErro);
    vboxControleErro.setLayoutX(50);
    vboxControleErro.setLayoutY(50);
    root.getChildren().add(vboxControleErro);

    Label porcentagemErro = new Label("Porcentagem do erro");
    porcentagemErro.setStyle(
        "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A; -fx-background-color: #FFFFFF");
    // Criando um ComboBox e adicionando itens a ele
    ComboBox<String> dropBoxTipoPorcentagemErro = new ComboBox<>();
    dropBoxTipoPorcentagemErro.getItems().addAll("0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%",
        "100%");
    dropBoxTipoPorcentagemErro.setValue("0%");
    transmissor.porcentagemErro(0); // seta o valor de porcentagemErro inicialmente como 0

    // Adicionando um listener para o valor selecionado
    dropBoxTipoPorcentagemErro.setOnAction(event -> {
      String selectedItem = dropBoxTipoPorcentagemErro.getSelectionModel().getSelectedItem();
      if (selectedItem.equals("0%")) {
        transmissor.porcentagemErro(0);
      } else if (selectedItem.equals("10%")) {
        transmissor.porcentagemErro(1);
      } else if (selectedItem.equals("20%")) {
        transmissor.porcentagemErro(2);
      } else if (selectedItem.equals("30%")) {
        transmissor.porcentagemErro(3);
      } else if (selectedItem.equals("40%")) {
        transmissor.porcentagemErro(4);
      } else if (selectedItem.equals("50%")) {
        transmissor.porcentagemErro(5);
      } else if (selectedItem.equals("60%")) {
        transmissor.porcentagemErro(6);
      } else if (selectedItem.equals("70%")) {
        transmissor.porcentagemErro(7);
      } else if (selectedItem.equals("80%")) {
        transmissor.porcentagemErro(8);
      } else if (selectedItem.equals("90%")) {
        transmissor.porcentagemErro(9);
      } else if (selectedItem.equals("100%")) {
        transmissor.porcentagemErro(10);
      }
    });

    VBox vboxPorcentagemErro = new VBox(5, porcentagemErro, dropBoxTipoPorcentagemErro);
    vboxPorcentagemErro.setLayoutX(900);
    vboxPorcentagemErro.setLayoutY(50);
    root.getChildren().add(vboxPorcentagemErro);

    Label labelQtdBitErrado = new Label("Quantidade de Bits Errados");
    labelQtdBitErrado.setStyle(
        "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A; -fx-background-color: #FFFFFF");
    // Criando um ComboBox e adicionando itens a ele
    ComboBox<String> dropBoxqtdBitErrado = new ComboBox<>();
    dropBoxqtdBitErrado.getItems().addAll("1", "2", "3");
    dropBoxqtdBitErrado.setValue("1");
    transmissor.qtdBitsErrados(1); // seta o valor de qtdBitsErrados inicialmente como 1

    // Adicionando um listener para o valor selecionado
    dropBoxqtdBitErrado.setOnAction(event -> {
      String selectedItem = dropBoxqtdBitErrado.getSelectionModel().getSelectedItem();
      if (selectedItem.equals("1")) {
        transmissor.qtdBitsErrados(1);
      } else if (selectedItem.equals("2")) {
        transmissor.qtdBitsErrados(2);
      } else if (selectedItem.equals("3")) {
        transmissor.qtdBitsErrados(3);
      }
    });

    VBox vboxQtdBitErrado = new VBox(5, labelQtdBitErrado, dropBoxqtdBitErrado);
    vboxQtdBitErrado.setLayoutX(200);
    vboxQtdBitErrado.setLayoutY(50);
    vboxQtdBitErrado.setAlignment(Pos.CENTER_RIGHT);
    root.getChildren().add(vboxQtdBitErrado);

    // Criando um ComboBox e adicionando itens a ele
    ComboBox<String> dropBoxControleFLuxo = new ComboBox<>();
    dropBoxControleFLuxo.getItems().addAll("Janela Deslizante de 1 Bit", "Janela Deslizante Go-Back-n",
        "Janela Deslizante Com Retransmissao Seletiva");
    dropBoxControleFLuxo.setValue("Janela Deslizante de 1 Bit");
    // Adicionando um listener para o valor selecionado
    dropBoxControleFLuxo.setOnAction(event -> {
      String selectedItem = dropBoxControleFLuxo.getSelectionModel().getSelectedItem();
      if (selectedItem.equals("Janela Deslizante de 1 Bit")) {
        transmissor.tipoControleFluxo(0);
        receptor.setTipoControleFluxo(0);
      } else if (selectedItem.equals("Janela Deslizante Go-Back-n")) {
        transmissor.tipoControleFluxo(1);
        receptor.setTipoControleFluxo(1);
      } else if (selectedItem.equals("Janela Deslizante Com Retransmissao Seletiva")) {
        transmissor.tipoControleFluxo(2);
        receptor.setTipoControleFluxo(2);
      }
    });
    dropBoxControleFLuxo.setLayoutX(50);
    dropBoxControleFLuxo.setLayoutY(600);
    Label controleFluxo = new Label("Controle de Fluxo");
    controleFluxo.setStyle(
        "#FFFFFF; -fx-font-size: 15px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #435D7A; -fx-border-radius: 10; -fx-text-fill: #435D7A; -fx-background-color: #FFFFFF");

    VBox vboxControleFluxo = new VBox(5, controleFluxo, dropBoxControleFLuxo);
    vboxControleFluxo.setLayoutX(50);
    vboxControleFluxo.setLayoutY(550);
    root.getChildren().add(vboxControleFluxo);

    Scene scene = new Scene(root, 1100, 700);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Camada Fisica");
    // Bloqueio da maximizacao da tela
    primaryStage.setResizable(false);
    // Exibicao da janela
    primaryStage.show();

    primaryStage.setOnCloseRequest(t -> {
      Platform.exit();
      System.exit(0);
    });
  }

  /*
   * ***************************************************************
   * Metodo: main.
   * Funcao: metodo para iniciar a aplicacao.
   * Parametros: padrao java.
   * Retorno: sem retorno.
   */
  public static void main(String[] args) {
    launch(args);
  }
}