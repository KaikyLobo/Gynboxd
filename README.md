# Gynboxd

App Android de avaliações sinceras de rolês em Goiânia (bares, cafés, festas e eventos), inspirado no Letterboxd.
Projeto integrador de ADS da PUC Goiás.

- **Protótipo navegável (Figma):** https://www.figma.com/design/ODLbdXuW6JyRj4dY58BtB1/Gynboxd?node-id=0-1&t=V78Kj7z509fiIWPg-1
- **Documento de escopo:** [`documentacao/Gynboxd_Documento_de_Escopo.docx`](documentacao/Gynboxd_Documento_de_Escopo.docx)

> Este README é o guia do projeto. Antes de criar uma tela nova ou mexer em algo
> compartilhado (cores, tema, barra de baixo), leia as seções **3** e **4**.

---

## Sumário
1. [Como rodar](#1-como-rodar)
2. [Telas e navegação](#2-telas-e-navegação)
3. [Padrão visual (o que reutilizar)](#3-padrão-visual-o-que-reutilizar)
4. [Como criar uma tela nova](#4-como-criar-uma-tela-nova)
5. [Estrutura de arquivos](#5-estrutura-de-arquivos)
6. [Critérios da disciplina → onde estão no código](#6-critérios-da-disciplina--onde-estão-no-código)
7. [Fluxo de Git do grupo](#7-fluxo-de-git-do-grupo)
8. [Pendências](#8-pendências)
9. [Problemas comuns](#9-problemas-comuns)

---

## 1. Como rodar

| Item | Valor |
|---|---|
| Linguagem | Java 11 |
| `minSdk` / `targetSdk` | 26 / 37 |
| Package | `br.edu.pucgo.gynboxd` |
| Bibliotecas | AppCompat, Material Design 3, ConstraintLayout, RecyclerView (vem junto com o Material) |
| View Binding | **ligado** (`buildFeatures { viewBinding = true }` em `app/build.gradle.kts`) |

1. Android Studio → **File → Open** → pasta do projeto (ou **Get from VCS** com o link do GitHub).
2. Espere o **Gradle Sync** terminar.
3. Escolha um emulador e clique em **▶ Run**. O app abre no **Início**.

Ainda não tem backend: todos os dados são de exemplo, criados direto nas Activities.

---

## 2. Telas e navegação

```
                 ┌──────────── barra inferior (todas as telas principais) ────────────┐
                 │   Início        Lugares          Diário             Perfil         │
                 └─────┬──────────────┬───────────────┬──────────────────┬────────────┘
                       │              │               │                  │
                 MainActivity    (em construção)  DiarioActivity     PerfilActivity
                       │                              │                  │
          toque num item│                 toque num item│   toque num favorito│
                       └──────────────────┬───────────┴──────────────────┘
                                          ▼
                                 EventoDetalheActivity
                         (provisória, até a tela 2 ficar pronta)
```

| # | Tela | Activity | Layout | Responsável |
|---|---|---|---|---|
| 1 | Início (home) | `MainActivity` | `activity_main.xml` | Matheus |
| 2 | Foco do evento | `EventoDetalheActivity` *(provisória)* | `activity_evento_detalhe.xml` | *a definir* |
| 3 | Seu diário (lista de rolês) | `DiarioActivity` | `activity_diario.xml` | Kevin |
| 4 | Perfil | `PerfilActivity` | `activity_perfil.xml` | Kaiky |
| — | Cafés, bares e festas (aba "Lugares") | *ainda não existe* | — | *a definir* |

**Como a navegação funciona**
- A barra inferior é a mesma em todas as telas principais. Toda a lógica dela fica em **`BaseActivity`**, no método `telaDaAba()`, que diz qual Activity cada aba abre.
- A troca de aba usa uma **Intent explícita** com `FLAG_ACTIVITY_REORDER_TO_FRONT`. Se a tela já estiver aberta, ela é reaproveitada em vez de criar outra cópia. Por isso o diário não perde o rolê que você adicionou quando você vai ao Perfil e volta.
- Para abrir o detalhe de qualquer item, todas as telas usam **`EventoDetalheActivity.criarIntent(...)`**. Ninguém precisa saber o nome dos extras.

---

## 3. Padrão visual (o que reutilizar)

O padrão veio dos reutilizáveis do Matheus (home). **Não crie cores, temas ou barras novas:** use os de baixo.

### 3.1 Cores — `res/values/colors.xml`

| Nome | Cor | Uso |
|---|---|---|
| `@color/bg` | `#0F1318` | fundo das telas |
| `@color/surface` | `#161B22` | cards, barra inferior, botões redondos |
| `@color/divider` | `#2A313A` | linhas divisórias e bordas |
| `@color/green` | `#00E054` | cor de destaque (logo, aba ativa, estrelas, botões) |
| `@color/text_primary` | `#FFFFFF` | texto principal |
| `@color/text_secondary` | `#8A95A3` | texto secundário, títulos de seção |
| `@color/header_perfil` | `#122B2B` | só a faixa do topo do Perfil |

**Regra:** nada de `#RRGGBB` direto no layout. Se precisar de uma cor nova, adicione em `colors.xml` e avise o grupo.

### 3.2 Tema — `res/values/themes.xml`
- `Theme.Gynboxd`: Material 3 **escuro**, fonte **Inter** (`res/font/`) e verde como cor primária. Ele é aplicado no app inteiro pelo `AndroidManifest.xml`, então nenhuma tela precisa de `android:theme`.
- `SectionTitle`: título de seção ("ATIVIDADE RECENTE", "LISTAS"...). Use `style="@style/SectionTitle"`.
- `Widget.Gynboxd.BottomNav`: estilo da barra inferior.

### 3.3 Estilos compartilhados — `res/values/styles.xml`

| Estilo | Para quê |
|---|---|
| `ShapeAppearance.Gynboxd.Circulo` | imagem redonda (`app:shapeAppearanceOverlay` em `ShapeableImageView`) |
| `ShapeAppearance.Gynboxd.Arredondado` | imagem com cantos de 6dp (capas) |
| `ThemeOverlay.Gynboxd.DatePicker` | calendário (`MaterialDatePicker`) com as cores do app |

Estilos usados por **uma tela só** ficam em `styles_<tela>.xml` (`styles_diario.xml`, `styles_perfil.xml`).

> ⚠️ **Pegadinha:** um estilo com ponto no nome (`Perfil.Numero`) faz o Android procurar um estilo "pai"
> chamado `Perfil`. Se ele não existir, o build falha com `resource style/Perfil not found`.
> Por isso, sempre coloque `parent=""` ou um parent real.

### 3.4 Textos — `res/values/strings*.xml`
- `strings.xml`: textos usados por várias telas (`app_name`, `voltar`, `tela_em_construcao`...).
- `strings_<tela>.xml`: textos de uma tela só (`strings_diario.xml`, `strings_perfil.xml`).
- Prefixe o nome com a tela para não colidir: `perfil_seguindo`, `titulo_diario`...

### 3.5 Componentes prontos para copiar

| Peça | De onde copiar |
|---|---|
| Barra superior (logo verde + botão redondo) | topo de `activity_main.xml` ou `activity_diario.xml` |
| Barra inferior + linha acima dela | final de `activity_diario.xml` (`divBottom` + `bottomNav`) |
| Card com borda | `item_diario.xml` (`MaterialCardView`) |
| Ícones | `res/drawable/ic_*.xml` (home, place, menu_book, person, search, notifications, add, voltar) |
| Imagens de exemplo | `poster_1..4`, `capa_1..5`, `capa_padrao` (gradientes) e `lugar1..4.png` |

---

## 4. Como criar uma tela nova

Exemplo: a tela **Lugares** (aba "Cafés, bares e festas").

**1) Layout** — `res/layout/activity_lugares.xml`
- Raiz `ConstraintLayout` com `android:background="@color/bg"`.
- Copie a barra superior e a barra inferior de `activity_diario.xml`.
- O conteúdo fica entre as duas, com `layout_height="0dp"` e constraints em cima e embaixo, assim ele se ajusta a qualquer tamanho de tela.
- Listas sempre com **`RecyclerView`**, nunca com `<include>` repetido.

**2) Activity** — `LugaresActivity.java`, herdando de **`BaseActivity`**:

```java
public class LugaresActivity extends BaseActivity {

    private ActivityLugaresBinding binding;   // gerado a partir de activity_lugares.xml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLugaresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        aplicarBarrasDoSistema(binding.getRoot(), true);          // true = tela tem barra inferior
        configurarNavegacao(binding.bottomNav, R.id.nav_lugares); // marca a aba desta tela

        // ... configurar RecyclerView, botões com lambda etc.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
```

**3) Manifest** — dentro de `<application>`:
```xml
<activity android:name=".LugaresActivity" android:exported="false" />
```

**4) Ligar a aba** — em `BaseActivity.telaDaAba()`:
```java
if (id == R.id.nav_lugares) return LugaresActivity.class;
```

**5) Lista (RecyclerView + ViewHolder)** — siga o modelo de `DestaqueAdapter.java`, que é o mais simples:
- uma classe modelo (`Destaque.java`);
- um adapter com ViewHolder que guarda o **Binding** do item (`ItemDestaqueBinding`);
- o clique é uma **interface** recebida no construtor e implementada com **lambda** na Activity.

**6) Abrir o detalhe de um item:**
```java
startActivity(EventoDetalheActivity.criarIntent(this,
        nome, subtitulo, EventoDetalheActivity.SEM_DATA, nota, R.drawable.poster_1));
```

**Regras do código** (são os critérios da disciplina):
- ❌ `findViewById` → ✅ View Binding
- ❌ `new View.OnClickListener() { ... }` → ✅ `v -> ...` (lambda). A única exceção é quando a interface tem mais de um método, como no `TabLayout`.
- ❌ `<include>` repetido, `ListView` → ✅ `RecyclerView` com ViewHolder
- ❌ `LinearLayout` aninhado para posicionar a tela → ✅ `ConstraintLayout`
- ❌ `Button`, `CardView` antigos → ✅ `MaterialButton`, `MaterialCardView`
- ✅ Dados que o usuário altera devem ser salvos em `onSaveInstanceState`, para não sumirem ao girar a tela (veja `DiarioActivity` e `PerfilActivity`).

---

## 5. Estrutura de arquivos

```
app/src/main/
├── AndroidManifest.xml              ← todas as Activities registradas aqui
├── java/br/edu/pucgo/gynboxd/
│   ├── BaseActivity.java            ← base de todas as telas (barras do sistema + barra inferior)
│   │
│   ├── MainActivity.java            ← 1. Início
│   ├── Atividade.java               │   modelo "atividade recente"
│   ├── AtividadeAdapter.java        │   lista vertical
│   ├── Destaque.java                │   modelo "em alta"
│   ├── DestaqueAdapter.java         │   lista horizontal
│   │
│   ├── EventoDetalheActivity.java   ← 2. Detalhe (provisória) + criarIntent()
│   │
│   ├── DiarioActivity.java          ← 3. Seu diário (abas, estatísticas, DatePicker)
│   ├── DiarioAdapter.java           │   lista com 2 tipos de linha (mês / rolê)
│   ├── EntradaDiario.java           │   modelo de um rolê
│   │
│   ├── PerfilActivity.java          ← 4. Perfil
│   ├── FavoritosAdapter.java        │   lista horizontal de favoritos
│   ├── ListaAdapter.java            │   lista de listas
│   └── ListaItem.java               │   modelo (favorito / lista)
│
└── res/
    ├── color/nav_item_color.xml     ← cor dos ícones da barra inferior (verde quando ativo)
    ├── drawable/                    ← ícones ic_*, imagens de exemplo, fundos bg_*
    ├── font/                        ← Inter (regular e bold)
    ├── layout/                      ← activity_<tela>.xml e item_<lista>.xml
    ├── menu/bottom_nav_menu.xml     ← as 4 abas da barra inferior
    └── values/
        ├── colors.xml               ← paleta (seção 3.1)
        ├── themes.xml               ← tema, SectionTitle, estilo da barra inferior
        ├── styles.xml               ← estilos compartilhados (formas, calendário)
        ├── styles_<tela>.xml        ← estilos de uma tela só
        ├── strings.xml              ← textos compartilhados
        └── strings_<tela>.xml       ← textos de uma tela só
```

**Convenção de nomes**
- Layout de tela: `activity_<tela>.xml`. Item de lista: `item_<coisa>.xml`.
- IDs em camelCase com prefixo do tipo: `tvNome`/`txtNome` (texto), `iv`/`img` (imagem), `rv`/`recycler` (lista), `btn` (botão).
- O View Binding gera o nome da classe a partir do layout: `activity_perfil.xml` vira `ActivityPerfilBinding`, e o id `btnSeguindo` vira `binding.btnSeguindo`.

---

## 6. Critérios da disciplina → onde estão no código

| Critério | Onde mostrar |
|---|---|
| **ConstraintLayout** responsivo | `activity_diario.xml`: 3 cards em *chain* horizontal. `activity_perfil.xml`: 4 números em *chain*. Listas com `0dp` entre topo e barra inferior |
| **Material Design 3** | tema `Theme.Material3.Dark`, `MaterialButton` (+, Seguindo, Voltar), `MaterialCardView` (cards e itens), `TabLayout`, `BottomNavigationView`, `ShapeableImageView`, `Snackbar` |
| **View Binding** | todas as Activities e todos os Adapters. Não há nenhum `findViewById` no projeto |
| **Lambdas** | cliques de botões e itens, barra inferior, `Collections.sort`, listener do DatePicker |
| **Intents explícitas + dados** | `EventoDetalheActivity.criarIntent()` envia os extras e a tela lê com valores padrão (sem crash se faltar extra). Troca de abas em `BaseActivity` |
| **Ciclo de vida / estabilidade** | `onSaveInstanceState` no Diário (lista e aba) e no Perfil (botão Seguir). DatePicker reanexado após rotação. `binding = null` no `onDestroy` |
| **RecyclerView + ViewHolder** | 5 adapters: `AtividadeAdapter`, `DestaqueAdapter`, `DiarioAdapter` (2 tipos de ViewHolder), `FavoritosAdapter`, `ListaAdapter` |
| **MaterialDatePicker** | botão **+** do Diário (`DiarioActivity.abrirDatePicker()`), com datas futuras bloqueadas |

---

## 7. Fluxo de Git do grupo

**Branches**
- `main`: versão oficial que funciona (é a branch padrão do GitHub). **Ninguém commita direto nela.**
- `integracao`: junção de todas as telas no padrão deste README. Depois de testada, é feito o merge dela na `main`.
- Uma branch por tarefa, criada a partir da `main` atualizada: `tela-lugares`, `ajuste-perfil`...
- As branches antigas (`master`, `tela-3`, `tela-perfil`) já estão dentro da `integracao` e podem ser apagadas depois do merge.

**Dia a dia (Android Studio)**
1. Antes de começar: troque para a `main` e atualize com **Ctrl+T** (Update Project).
2. Crie sua branch: **Ctrl+Alt+N** → nome → ✔ Checkout branch.
3. Trabalhe e rode o app.
4. Commit: **Ctrl+K**. Marque **só** arquivos de `app/`, `gradle/` e `README.md`. Escreva o que fez e clique em **Commit and Push**.
5. No GitHub, abra um **Pull Request** da sua branch para a `main`, e outra pessoa do grupo revisa e faz o merge.

**Nunca suba:** `.idea/`, `.gradle/`, `build/`, `local.properties`, `*.iml`. São arquivos do *seu* PC (caminho do SDK, emulador escolhido, abas abertas). Eles já estão no `.gitignore`. Se aparecerem para commit, desmarque.

**Para não dar conflito**
- Não reescreva a tela de outra pessoa. Crie a sua Activity e o seu layout.
- Arquivos compartilhados (`colors.xml`, `themes.xml`, `styles.xml`, `strings.xml`, `BaseActivity.java`, `bottom_nav_menu.xml`, `AndroidManifest.xml`): **só adicione linhas**, não apague nem renomeie sem combinar com o grupo.

---

## 8. Pendências

- [ ] **Tela 2 — Foco do evento.** Substituir o conteúdo de `EventoDetalheActivity` / `activity_evento_detalhe.xml` pela tela do Figma, **mantendo o método `criarIntent()`**. Assim as outras telas continuam funcionando sem mudança.
- [ ] **Tela Lugares** (aba "Cafés, bares e festas"): seguir a seção 4.
- [ ] Trocar as imagens de exemplo (gradientes) por fotos reais.
- [ ] Backend: API REST (Spring Boot + MySQL). Quando existir, os dados de exemplo saem das Activities.

---

## 9. Problemas comuns

| Erro / sintoma | Causa | Solução |
|---|---|---|
| `resource style/X not found` | estilo com ponto no nome sem pai | `parent=""` no estilo (seção 3.3) |
| `cannot find symbol ActivityXxxBinding` | View Binding desligado ou build antigo | confira `viewBinding = true` e rode **Build → Rebuild Project** |
| `illegal character: '﻿'` | arquivo `.java` salvo com BOM (comum ao copiar do Bloco de Notas) | abra o arquivo, clique em **UTF-8** na barra de baixo do Android Studio → *Remove BOM* |
| "Your local changes would be overwritten by checkout" em `.idea/...` | arquivo do Android Studio no Git | **Force Checkout** (são só configurações locais) |
| Emulador: "Not enough space to create userdata partition" | pouco espaço em disco | Device Manager → ⋮ → Edit → *Internal storage* menor (ex.: 6 GB) → Wipe Data |
| App abre e fecha | erro em tempo de execução | aba **Logcat**, filtre por `AndroidRuntime` e veja a primeira linha `Caused by` |
