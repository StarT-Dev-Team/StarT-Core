{
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = {
    self,
    nixpkgs,
    ...
  }: 
  let
    system = "x86_64-linux";
    pkgs = nixpkgs.legacyPackages.${system};
  in {    
    # Development shell environment for running the texture generator in python
    devShells.${system} = {
      default = pkgs.mkShell {
        name = "start-core-dreamlink-texture-dev";
        packages = with pkgs; [
          python311
          stdenv.cc.cc.lib
          python311Packages.numpy
          python311Packages.pillow
        ];

        LD_LIBRARY_PATH = "${pkgs.stdenv.cc.cc.lib}/lib";
      };
    };
  };
}
